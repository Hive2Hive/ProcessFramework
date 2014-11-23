package org.hive2hive.processframework;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.hive2hive.processframework.decorators.AsyncComponent;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for all process components. Keeps track of a process components' most essential
 * properties and functionalities.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by this {@code ProcessComponent}.
 */
public abstract class ProcessComponent<T> implements IProcessComponent<T> {

	private static Logger logger = LoggerFactory.getLogger(ProcessComponent.class);

	// pausing only possible from another thread
	protected volatile boolean isPaused;

	private String name;
	private final String id;
	private ProcessState state;
	private final List<IProcessComponentListener> listeners;
	private ProcessComposite<?> parent;

	private boolean isRollbacking;
	private boolean requiresRollback;

	private AsyncComponent<T> asyncComponent = null;

	public ProcessComponent() {
		this.id = UUID.randomUUID().toString();
		this.name = String.format("Process Component ID: %s", id);
		this.state = ProcessState.READY;
		this.listeners = new ArrayList<IProcessComponentListener>();
	}

	/**
	 * Starts or resumes the execution of this {@code ProcessComponent}.
	 * Upon successful execution, returns the result of type {@code T} and all attached
	 * {@link IProcessComponentListener}s notify the success. Otherwise, a {@link ProcessExecutionException}
	 * is thrown and all attached {@link IProcessComponentListener}s notify the failure.
	 * 
	 * @return The computed result of type {@code T}.
	 */
	public final T execute() throws InvalidProcessStateException, ProcessExecutionException {
		if (state != ProcessState.READY && state != ProcessState.ROLLBACK_SUCCEEDED
				&& state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(this, state);
		}
		logger.debug("Executing '{}'.", this);
		setState(ProcessState.EXECUTING);
		notifyListeners(ProcessState.EXECUTING);
		isRollbacking = false;

		T result;
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

	public final Future<T> executeAsync() throws InvalidProcessStateException, ProcessExecutionException {
		// wrap this component with an AsyncComponent decorator
		return getAsyncComponent().execute();
	}

	/**
	 * Starts or resumes the rollback of this {@code ProcessComponent}.
	 * Upon successful rollback, returns the result of type {@code T} and all attached
	 * {@link IProcessComponentListener}s notify the success. Otherwise, a {@link ProcessRollbackException} is
	 * thrown and all attached {@link IProcessComponentListener}s notify the failure.
	 * 
	 * @return The computed result of type {@code T}.
	 */
	@Override
	public final T rollback() throws InvalidProcessStateException, ProcessRollbackException {
		if (state != ProcessState.EXECUTION_FAILED && state != ProcessState.EXECUTION_SUCCEEDED
				&& state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(this, state);
		}
		// only rollback if component was marked
		if (!requiresRollback) {
			return null;
		}

		logger.debug("Rollbacking '{}'.", this);
		setState(ProcessState.ROLLBACKING);
		notifyListeners(ProcessState.ROLLBACKING);
		isRollbacking = true;

		T result;
		try {
			result = doRollback();
			setState(ProcessState.ROLLBACK_SUCCEEDED);
			notifyListeners(ProcessState.ROLLBACK_SUCCEEDED);
		} catch (ProcessRollbackException ex) {
			setState(ProcessState.ROLLBACK_FAILED);
			notifyListeners(ProcessState.ROLLBACK_FAILED);
			throw ex;
		}
		return result;
	}

	public final Future<T> rollbackAsync() throws InvalidProcessStateException, ProcessRollbackException {
		// wrap this component with an AsyncComponent decorator
		return getAsyncComponent().rollback();
	}

	@Override
	public final void pause() throws InvalidProcessStateException {
		if (state != ProcessState.EXECUTING && state != ProcessState.ROLLBACKING) {
			throw new InvalidProcessStateException(this, state);
		}
		logger.debug("Pausing '{}'.", this);

		isPaused = true;
		setState(ProcessState.PAUSED);
		notifyListeners(ProcessState.PAUSED);
	}

	@Override
	public final void resume() throws InvalidProcessStateException, ProcessExecutionException,
			ProcessRollbackException {
		if (state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(this, state);
		}
		logger.debug("Resuming '{}'.", this);

		isPaused = false;
		if (!isRollbacking) {
			execute();
		} else {
			rollback();
		}
	}

	@SuppressWarnings("unchecked")
	private AsyncComponent<T> getAsyncComponent() {
		if (asyncComponent == null) {

			// distinguish components that are already wrapped with AsyncComponent
			if (this instanceof AsyncComponent<?>) {
				asyncComponent = ((AsyncComponent<T>) this);
			} else {
				asyncComponent = new AsyncComponent<T>(this);
			}
		}
		return asyncComponent;
	}

	/**
	 * Template method responsible for the execution.
	 * If a failure occurs during this {@code ProcessComponent}'s execution, a
	 * {@link ProcessExecutionException} is
	 * thrown.
	 * 
	 * @throws InvalidProcessStateException If this {@code ProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure occured during this {@code ProcessComponent}'s
	 *             execution.
	 */
	protected abstract T doExecute() throws InvalidProcessStateException, ProcessExecutionException;

	/**
	 * Template method responsible for the rollback.
	 * If a failure occurs during this {@code ProcessComponent}'s rollback, a {@link ProcessRollbackException}
	 * is
	 * thrown.
	 * 
	 * @throws InvalidProcessStateException If this {@code ProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during this {@code ProcessComponent}'s rollback.
	 */
	protected abstract T doRollback() throws InvalidProcessStateException, ProcessRollbackException;

	protected void setRequiresRollback(boolean requiresRollback) {
		this.requiresRollback = requiresRollback;
	}

	@Override
	public void setParent(ProcessComposite<?> parent) {
		this.parent = parent;
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
	public ProcessComposite<?> getParent() {
		return this.parent;
	}

	@Override
	public boolean getRollbackRequired() {
		return this.requiresRollback;
	}

	@Override
	public String toString() {
		return getName();
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
