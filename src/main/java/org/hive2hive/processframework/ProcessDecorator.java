package org.hive2hive.processframework;

import java.util.List;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;

/**
 * Abstract base class for all decorator {@link ProcessComponent}s.
 * These decorators provide additional behavior or state to existing {@link ProcessComponent}s.
 * This abstract base class just delegates all calls to the wrapped/decorated {@link ProcessComponent}.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessDecorator extends ProcessComponent {

	protected final ProcessComponent decoratedComponent;

	/**
	 * Creates a {@code ProcessDecorator} that wraps/decorates the provided {@link ProcessComponent}.
	 * 
	 * @param decoratedComponent The {@link ProcessComponent} to be wrapped/decorated by this
	 *            {@code ProcessDecorator}.
	 */
	public ProcessDecorator(ProcessComponent decoratedComponent) {
		this.decoratedComponent = decoratedComponent;
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void start() throws InvalidProcessStateException, ProcessRollbackException {
		decoratedComponent.start();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void cancel(RollbackReason reason) throws InvalidProcessStateException, ProcessRollbackException {
		decoratedComponent.cancel(reason);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void pause() throws InvalidProcessStateException {
		decoratedComponent.pause();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void resume() throws InvalidProcessStateException {
		decoratedComponent.resume();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void await() throws InterruptedException {
		decoratedComponent.await();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void await(long timeout) throws InterruptedException {
		decoratedComponent.await(timeout);
	}

	/**
	 * Default decorator implementation responsible for the execution.
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException,
			ProcessRollbackException {
		decoratedComponent.doExecute();
	}

	/**
	 * Default decorator implementation responsible for the rollback.
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void doRollback(RollbackReason reason) throws InvalidProcessStateException,
			ProcessRollbackException {
		decoratedComponent.doRollback(reason);
	}

	/**
	 * Default decorator implementation responsible for the execution or rollback pausing.
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void doPause() throws InvalidProcessStateException {
		decoratedComponent.doPause();
	}

	/**
	 * Default decorator implementation responsible for the execution resuming.
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		decoratedComponent.doResumeExecution();
	}

	/**
	 * Default decorator implementation responsible for the rollback resuming.
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		decoratedComponent.doResumeRollback();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void succeed() {
		decoratedComponent.succeed();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void fail(RollbackReason reason) {
		decoratedComponent.fail(reason);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	protected void setParent(Process parent) {
		decoratedComponent.setParent(parent);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public Process getParent() {
		return decoratedComponent.getParent();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public void setName(String name) {
		decoratedComponent.setName(name);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public String getName() {
		return decoratedComponent.getName();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public String getID() {
		return decoratedComponent.getID();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public double getProgress() {
		return decoratedComponent.getProgress();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public ProcessState getState() {
		return decoratedComponent.getState();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public synchronized void attachListener(IProcessComponentListener listener) {
		decoratedComponent.attachListener(listener);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public synchronized void detachListener(IProcessComponentListener listener) {
		decoratedComponent.detachListener(listener);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public List<IProcessComponentListener> getListeners() {
		return decoratedComponent.getListeners();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public String toString() {
		return decoratedComponent.toString();
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public boolean equals(Object obj) {
		return decoratedComponent.equals(obj);
	}

	/**
	 * Default decorator implementation:
	 * Just delegates the call to the wrapped/decorated {@link ProcessComponent}.
	 */
	@Override
	public int hashCode() {
		return decoratedComponent.hashCode();
	}

}