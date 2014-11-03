package org.hive2hive.processframework;

import java.util.List;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;

/**
 * Abstract base class for all decorators that decorate {@link IProcessComponent}s.
 * These decorators provide additional behavior or state to existing implementations of
 * {@link IProcessComponent}s.
 * This abstract base class just delegates all calls to the wrapped/decorated {@link IProcessComponent}
 * implementation.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessDecorator<T> extends ProcessComponent<T> {

	protected final IProcessComponent<T> decoratedComponent;

	/**
	 * Creates a {@code ProcessDecorator} that wraps/decorates the provided {@link IProcessComponent}
	 * implementation.
	 * 
	 * @param decoratedComponent The {@link IProcessComponent} implementation to be wrapped/decorated by this
	 *            {@code ProcessDecorator}.
	 */
	public ProcessDecorator(IProcessComponent<T> decoratedComponent) {
		this.decoratedComponent = decoratedComponent;
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public T execute() throws InvalidProcessStateException, ProcessExecutionException {
		return decoratedComponent.execute();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void rollback() throws InvalidProcessStateException, ProcessRollbackException {
		decoratedComponent.rollback();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void pause() throws InvalidProcessStateException {
		decoratedComponent.pause();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void resume() throws InvalidProcessStateException {
		decoratedComponent.resume();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void await() throws InterruptedException {
		decoratedComponent.await();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void await(long timeout) throws InterruptedException {
		decoratedComponent.await(timeout);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public Process<?> getParent() {
		return decoratedComponent.getParent();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public void setName(String name) {
		decoratedComponent.setName(name);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public String getName() {
		return decoratedComponent.getName();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public String getID() {
		return decoratedComponent.getID();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public double getProgress() {
		return decoratedComponent.getProgress();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public ProcessState getState() {
		return decoratedComponent.getState();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public synchronized void attachListener(IProcessComponentListener listener) {
		decoratedComponent.attachListener(listener);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public synchronized void detachListener(IProcessComponentListener listener) {
		decoratedComponent.detachListener(listener);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public List<IProcessComponentListener> getListeners() {
		return decoratedComponent.getListeners();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public String toString() {
		return decoratedComponent.toString();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public boolean equals(Object obj) {
		return decoratedComponent.equals(obj);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link IProcessComponent} implementation.
	 */
	@Override
	public int hashCode() {
		return decoratedComponent.hashCode();
	}

}