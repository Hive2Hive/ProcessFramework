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
import org.hive2hive.processframework.utils.TestProcessComponentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all process components. Keeps track of a process components' most essential
 * properties and functionalities.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessComponent<T> implements IProcessComponent<T> {

	private static Logger logger = LoggerFactory.getLogger(ProcessComponent.class);

	private String name;
	private final String id;
	private ProcessState state;
	private double progress;
	private final List<IProcessComponentListener> listeners;
	private Process<?> parent;
	private T result;

	private boolean isRollbacking;
	private boolean requiresRollback;

	public ProcessComponent() {
		this.id = UUID.randomUUID().toString();
		this.name = String.format("[Process Component ID: %s]", id);
		this.state = ProcessState.READY;
		this.progress = 0.0;
		this.listeners = new ArrayList<IProcessComponentListener>();
	}

	/**
	 * Starts the execution of this {@code ProcessComponent}.
	 * Upon successful execution, all attached {@link TestProcessComponentListener}s notify the success.
	 * <ul>
	 * <li>In case of a failure during the execution, this {@code ProcessComponent} automatically cancels and
	 * starts its rollback.</li>
	 * <li>In case of a failure during the rollback, this method throws a {@link ProcessRollbackException}.</li>
	 * </ul>
	 * In both cases, all attached {@link TestProcessComponentListener}s notify the failure.
	 */
	@Override
	public final T execute() throws InvalidProcessStateException, ProcessExecutionException {
		if (state != ProcessState.READY && state != ProcessState.ROLLBACK_SUCCEEDED) {
			throw new InvalidProcessStateException(state);
		}
		logger.debug("Executing '{}'.", this);
		setState(ProcessState.EXECUTING);
		notifyListeners(ProcessState.EXECUTING);
		isRollbacking = false;

		try {
			result = doExecute();
			setState(ProcessState.EXECUTION_SUCCEEDED);
			notifyListeners(ProcessState.EXECUTION_SUCCEEDED);
		} catch (ProcessExecutionException ex) {
			setState(ProcessState.EXECUTION_FAILED);
			notifyListeners(ProcessState.EXECUTION_FAILED);
			throw ex;
		}
		return result;
	}

	/**
	 * Starts the execution of this {@code ProcessComponent}.
	 * In case of a failure during the rollback, this method throws a {@link ProcessRollbackException}.
	 * In both cases, all attached {@link TestProcessComponentListener}s notify the failure.
	 */
	@Override
	public final void rollback() throws InvalidProcessStateException, ProcessRollbackException {
		if (state != ProcessState.EXECUTION_FAILED && state != ProcessState.EXECUTION_SUCCEEDED
				&& state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}
		// only rollback if component was marked
		if (!requiresRollback) {
			return;
		}

		logger.debug("Rollbacking '{}'.", this);
		setState(ProcessState.ROLLBACKING);
		notifyListeners(ProcessState.ROLLBACKING);
		isRollbacking = true;

		try {
			doRollback();
			setState(ProcessState.ROLLBACK_SUCCEEDED);
			notifyListeners(ProcessState.ROLLBACK_SUCCEEDED);
		} catch (ProcessRollbackException ex) {
			setState(ProcessState.ROLLBACK_FAILED);
			notifyListeners(ProcessState.ROLLBACK_FAILED);
			throw ex;
		}
	}

	@Override
	public final void pause() throws InvalidProcessStateException {
		if (state != ProcessState.EXECUTING && state != ProcessState.ROLLBACKING) {
			throw new InvalidProcessStateException(state);
		}
		logger.debug("Pausing '{}'.", this);
		setState(ProcessState.PAUSED);
		notifyListeners(ProcessState.PAUSED);
		
		doPause();
	}

	@Override
	public final void resume() throws InvalidProcessStateException {
		if (state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}
		logger.debug("Resuming '{}'.", this);
		
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

		if (hasFinished())
			return;

		final CountDownLatch latch = new CountDownLatch(1);

		ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
		ScheduledFuture<?> handle = executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (hasFinished())
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

	private boolean hasFinished() {
		return state == ProcessState.EXECUTION_SUCCEEDED || state == ProcessState.EXECUTION_FAILED
				|| state == ProcessState.ROLLBACK_SUCCEEDED || state == ProcessState.ROLLBACK_FAILED;
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
	protected abstract T doExecute() throws InvalidProcessStateException, ProcessExecutionException;

	/**
	 * Template method responsible for the rollback.
	 * If a failure occurs during this process component's rollback, a {@link ProcessRollbackException} is
	 * thrown.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during this process component's rollback.
	 */
	protected abstract void doRollback() throws InvalidProcessStateException, ProcessRollbackException;

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

	@Override
	public void setParent(Process<?> parent) {
		this.parent = parent;
	}
	
	protected void setRequiresRollback(boolean requiresRollback) {
		this.requiresRollback = requiresRollback;
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
		this.listeners.add(listener);

		// fire event if it already occurred
		if (state == ProcessState.EXECUTING) {
			listener.onExecuting(new ProcessEventArgs(this));
		} else if (state == ProcessState.ROLLBACKING) {
			listener.onRollbacking(new ProcessEventArgs(this));
		} else if (state == ProcessState.PAUSED) {
			listener.onPaused(new ProcessEventArgs(this));
		} else if (state == ProcessState.EXECUTION_SUCCEEDED) {
			listener.onExecutionSucceeded(new ProcessEventArgs(this));
		} else if (state == ProcessState.EXECUTION_FAILED) {
			listener.onExecutionFailed(new ProcessEventArgs(this));
		} else if (state == ProcessState.ROLLBACK_SUCCEEDED) {
			listener.onRollbackSucceeded(new ProcessEventArgs(this));
		} else if (state == ProcessState.ROLLBACK_FAILED) {
			listener.onRollbackFailed(new ProcessEventArgs(this));
		}
	}

	@Override
	public synchronized void detachListener(IProcessComponentListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public List<IProcessComponentListener> getListeners() {
		return this.listeners;
	}

	@Override
	public Process<?> getParent() {
		return this.parent;
	}
	
	@Override
	public boolean getRollbackRequired() {
		return this.requiresRollback;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ProcessComponent))
			return false;

		ProcessComponent<?> other = (ProcessComponent<?>) obj;
		return id.equals(other.getID());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	private void setState(ProcessState state) {
		this.state = state;
	}

	private void notifyListeners(ProcessState event) {
		for (IProcessComponentListener listener : this.listeners) {
			switch (event) {
				case EXECUTING:
					listener.onExecuting(new ProcessEventArgs(this));
					break;
				case ROLLBACKING:
					listener.onRollbacking(new ProcessEventArgs(this));
					break;
				case PAUSED:
					listener.onPaused(new ProcessEventArgs(this));
					break;
				case EXECUTION_SUCCEEDED:
					listener.onExecutionSucceeded(new ProcessEventArgs(this));
					break;
				case EXECUTION_FAILED:
					listener.onExecutionFailed(new ProcessEventArgs(this));
					break;
				case ROLLBACK_SUCCEEDED:
					listener.onRollbackSucceeded(new ProcessEventArgs(this));
					break;
				case ROLLBACK_FAILED:
					listener.onRollbackFailed(new ProcessEventArgs(this));
					break;
				default:
					break;
			}
		}
	}

}
