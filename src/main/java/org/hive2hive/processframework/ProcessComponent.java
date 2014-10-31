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
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
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

	/**
	 * Starts the execution of this {@code ProcessComponent} and therefore triggers its execution. Upon
	 * successful execution,
	 * all attached {@link ProcessComponentListener}s notify the success.
	 * <ul>
	 * <li>In case of a failure during the execution, this {@code ProcessComponent} automatically cancels and
	 * starts its rollback.</li>
	 * <li>In case of a failure during the rollback, this method throws a {@link ProcessRollbackException}.</li>
	 * </ul>
	 * In both cases, all attached {@link ProcessComponentListener}s notify the failure.
	 */
	@Override
	public void start() throws InvalidProcessStateException, ProcessRollbackException {
		if (state != ProcessState.READY) {
			throw new InvalidProcessStateException(state);
		}
		setState(ProcessState.EXECUTING);
		isRollbacking = false;

		try {
			doExecute();
			succeed();
		} catch (ProcessExecutionException ex1) {
			try {
				cancel(ex1.getRollbackReason());
			} catch (ProcessRollbackException ex2) {
				throw ex2;
			}
		} catch (ProcessRollbackException ex2) {
			throw ex2;
		}
	}

	/**
	 * Cancels the execution of this {@code ProcessComponent} and therefore triggers its rollback.
	 * In case of a failure during the rollback, this method throws a {@link ProcessRollbackException}.
	 * In both cases, all attached {@link ProcessComponentListener}s notify the failure.
	 */
	@Override
	public void cancel(RollbackReason reason) throws InvalidProcessStateException, ProcessRollbackException {
		if (state != ProcessState.EXECUTING && state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}
		setState(ProcessState.ROLLBACKING);
		isRollbacking = true;

		try {
			doRollback(reason);
		} catch (ProcessRollbackException e) {
			throw e;
		} finally {
			fail(reason);
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
		// TODO don't distinguish between executing and rollbacking state, each component should be able to
		// decide itself (decorators must implement both methods but cannot decide, they can just forward
		// resume())
		if (!isRollbacking) {
			setState(ProcessState.EXECUTING);
			doResumeExecution();
		} else {
			setState(ProcessState.ROLLBACKING);
			doResumeRollback();
		}
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

	/**
	 * Template method responsible for the execution.
	 * If a failure occurs during this process component's execution, a {@link ProcessExecutionException} is
	 * thrown.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure occured during this process component's execution.
	 */
	protected abstract void doExecute() throws InvalidProcessStateException, ProcessExecutionException, ProcessRollbackException;

	/**
	 * Template method responsible for the rollback.
	 * If a failure occurs during this process component's rollback, a {@link ProcessRollbackException} is
	 * thrown.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during this process component's rollback.
	 */
	protected abstract void doRollback(RollbackReason reason) throws InvalidProcessStateException,
			ProcessRollbackException;

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
	 * @throws InvalidProcessStateException If this component is in an invalid state for this operation.
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

	@Override
	public Process getParent() {
		return parent;
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

	private void setState(ProcessState state) {
		this.state = state;
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
