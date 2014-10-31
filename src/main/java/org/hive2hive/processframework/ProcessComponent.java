package org.hive2hive.processframework;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;

/**
 * Abstract base class for all process components. Keeps track of a process components' most essential
 * properties and functionalities.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessComponent implements IProcessComponent {

	// private static final Logger logger = LoggerFactory.getLogger(ProcessComponent.class);

	private String name;
	private final String id;
	private ProcessState state;
	private double progress;
	private final List<IProcessComponentListener> listener;

	private Process parent;
	private boolean isRollbacking;
	private RollbackReason reason;

	protected ProcessComponent() {
		this.id = UUID.randomUUID().toString();
		this.name = String.format("[Process Component ID: %s]", id);
		this.state = ProcessState.READY;
		this.progress = 0.0;
		this.listener = new ArrayList<IProcessComponentListener>();
	}

	@Override
	public void start() throws InvalidProcessStateException {
		// logger.debug("Executing '{}'.", this.getClass().getSimpleName());

		if (state != ProcessState.READY) {
			throw new InvalidProcessStateException(state);
		}
		setState(ProcessState.EXECUTING);
		isRollbacking = false;

		try {
			doExecute();
			succeed();
		} catch (ProcessExecutionException e) {
			cancel(e.getRollbackReason());
		}
	}

	@Override
	public void pause() throws InvalidProcessStateException {
		if (state != ProcessState.EXECUTING && state != ProcessState.ROLLBACKING) {
			throw new InvalidProcessStateException(state);
		}
		setState(ProcessState.PAUSED);
		doPause();
	}

	@Override
	public void resume() throws InvalidProcessStateException {
		if (state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}
		// TODO don't distinguish between running and rollback state, each component should be able to decide
		// itself (decorators must implement both methods but cannot decide, they can just forward resume())
		if (!isRollbacking) {
			setState(ProcessState.EXECUTING);
			doResumeExecution();
		} else {
			setState(ProcessState.ROLLBACKING);
			doResumeRollback();
		}
	}

	@Override
	public void cancel(RollbackReason reason) throws InvalidProcessStateException {
		if (state != ProcessState.EXECUTING && state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}

		// inform parent (if exists and not informed yet)
		if (parent != null && parent.getState() != ProcessState.ROLLBACKING) {
			getParent().cancel(reason);
		} else {

			// no parent, or called from parent
			setState(ProcessState.ROLLBACKING);
			// logger.warn("Rolling back '{}'. Reason: '{}'.", this.getClass().getSimpleName(),
			// reason.getHint());

			doRollback(reason);
		}

		fail(reason);
	}

	/**
	 * Template method responsible for the execution.</br>
	 * If a failure is detected, a {@link ProcessExecutionException} is thrown and the component and its
	 * enclosing process component composite, if any, get cancelled and rolled back.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure is detected during the execution.
	 */
	protected abstract void doExecute() throws InvalidProcessStateException, ProcessExecutionException;

	/**
	 * Template method responsible for the execution or rollback pausing.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 */
	protected abstract void doPause() throws InvalidProcessStateException;

	/**
	 * Template method responsible for the execution resuming.
	 * 
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	protected abstract void doResumeExecution() throws InvalidProcessStateException;

	/**
	 * Template method responsible for the rollback resuming.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 */
	protected abstract void doResumeRollback() throws InvalidProcessStateException;

	/**
	 * Template method responsible for the rollback.
	 * 
	 * @param reason The reason for the cancellation or fail.
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	protected abstract void doRollback(RollbackReason reason) throws InvalidProcessStateException;

	/**
	 * If in {@link ProcessState#EXECUTING}, this {@code ProcessComponent} succeeds, changes its state to
	 * {@link ProcessState#SUCCEEDED} and notifies all interested listeners.
	 */
	protected void succeed() {
		if (state == ProcessState.EXECUTING) {
			setState(ProcessState.SUCCEEDED);
			notifySucceeded();
		}
	}

	/**
	 * If in {@link ProcessState#ROLLBACKING}, this {@code ProcessComponent} succeeds, changes its state to
	 * {@link ProcessState#FAILED} and notifies all interested listeners.
	 */
	protected void fail(RollbackReason reason) {
		if (state == ProcessState.ROLLBACKING) {
			setState(ProcessState.FAILED);
			this.reason = reason;
			notifyFailed(reason);
		}
	}

	protected void setParent(Process parent) {
		this.parent = parent;
	}

	protected Process getParent() {
		return parent;
	}
	
	private void setState(ProcessState state) {
		this.state = state;
	}

	@Override
	public void await() throws InterruptedException {
		await(-1);
	}

	@Override
	public void await(long timeout) throws InterruptedException {

		if (state == ProcessState.SUCCEEDED || state == ProcessState.FAILED)
			return;

		final CountDownLatch latch = new CountDownLatch(1);

		ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
		ScheduledFuture<?> handle = executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (state == ProcessState.SUCCEEDED || state == ProcessState.FAILED)
					latch.countDown();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);

		// blocking wait for completion or interruption
		try {
			if (timeout < 0) {
				latch.await();
			} else {
				boolean success = latch.await(timeout, TimeUnit.MILLISECONDS);
				if (!success) {
					throw new InterruptedException("Waiting for process timed out.");
				}
			}
		} catch (InterruptedException e) {
			// logger.error("Interrupted while waiting for process.", e);
			throw e;
		} finally {
			handle.cancel(true);
		}
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public double getProgress() {
		return this.progress;
	}

	@Override
	public ProcessState getState() {
		return this.state;
	}

	@Override
	public synchronized void attachListener(IProcessComponentListener listener) {
		this.listener.add(listener);

		// if process component completed already
		if (state == ProcessState.SUCCEEDED) {
			listener.onSucceeded();
		}
		if (state == ProcessState.FAILED) {
			listener.onFailed(reason);
		}
	}

	@Override
	public synchronized void detachListener(IProcessComponentListener listener) {
		this.listener.remove(listener);
	}

	@Override
	public List<IProcessComponentListener> getListeners() {
		return listener;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ProcessComponent))
			return false;

		ProcessComponent other = (ProcessComponent) obj;
		return id.equals(other.getID());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	private void notifySucceeded() {
		for (IProcessComponentListener listener : this.listener) {
			listener.onSucceeded();
		}
	}

	private void notifyFailed(RollbackReason reason) {
		for (IProcessComponentListener listener : this.listener) {
			listener.onFailed(reason);
		}
	}

}
