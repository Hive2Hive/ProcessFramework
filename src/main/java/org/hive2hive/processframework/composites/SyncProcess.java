package org.hive2hive.processframework.composites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.hive2hive.processframework.ProcessComposite;
import org.hive2hive.processframework.decorators.AsyncComponent;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * A {@link ProcessComposite} that awaits asynchronous components before completion.
 * Traverses its components in preorder (i.e., left-to-right).
 * 
 * @author Christian Lüthold
 *
 */
public final class SyncProcess extends ProcessComposite<Void> {

	private List<IProcessComponent<?>> components = new ArrayList<IProcessComponent<?>>();

	private List<Future<?>> asyncExecutions = new ArrayList<Future<?>>();
	private List<Future<?>> asyncRollbacks = new ArrayList<Future<?>>();

	private IProcessComponent<?> next = null;
	private IProcessComponent<?> last = null;
	private int executionIndex;
	private int rollbackIndex;
	
	@Override
	protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {

		// don't use iterator, as component list might be modified during execution
		// also, execution must be able to resume at correct position after pause
		if (next != null) {
			executionIndex = components.indexOf(next);
		} else {
			executionIndex = 0;
		}
		
		while (executionIndex < components.size() && !isPaused) {

			checkForAsyncExecutionFailure(asyncExecutions);

			next = components.get(executionIndex);
			if (next instanceof AsyncComponent<?>) {
				Future<?> async = ((AsyncComponent<?>) next).execute();
				asyncExecutions.add(async);
			} else {
				next.execute();
			}
			executionIndex++;
		}
		
		if (!isPaused) {
			// await async execution components
			for (Future<?> async : asyncExecutions) {
				awaitAsyncExecution(async);
			}
			asyncExecutions.clear();
		}

		return null;
	}

	@Override
	protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {

		// don't use iterator, as component list might be modified during rollback
		// also, rollback must be able to resume at correct position after pause
		if (last != null) {
			rollbackIndex = components.indexOf(last);
		} else {
			rollbackIndex = components.size() - 1;
		}
		
		while (rollbackIndex >= 0 && !isPaused) {

			checkForAsyncRollbackFailure(asyncRollbacks);

			last = components.get(rollbackIndex);
			if (last instanceof AsyncComponent<?>) {
				Future<?> async = ((AsyncComponent<?>) last).rollback();
				asyncRollbacks.add(async);
			} else {
				last.rollback();
			}
			rollbackIndex--;
		}

		if (!isPaused) {
			// await async rollback components
			for (Future<?> async : asyncRollbacks) {
				awaitAsyncRollback(async);
			}
			asyncRollbacks.clear();
		}

		return null;
	}

	@Override
	protected void doAdd(IProcessComponent<?> component) {
		components.add(component);
	}

	@Override
	protected void doAdd(int index, IProcessComponent<?> component) {
		components.add(index, component);
	}

	@Override
	protected void doRemove(IProcessComponent<?> component) {
		components.remove(component);
	}

	@Override
	public List<IProcessComponent<?>> getComponents() {
		return Collections.unmodifiableList(components);
	}

	@Override
	public IProcessComponent<?> getComponent(int index) {
		return components.get(index);
	}

	@Override
	public double getProgress() {
		
		double progress = 0.0;
		double weigth = 1 / components.size();
		
		for (IProcessComponent<?> comp : components) {
			progress += weigth * comp.getProgress();
		}
		
		return progress;
	}

	private void checkForAsyncExecutionFailure(List<Future<?>> asyncs) throws ProcessExecutionException {

		for (Future<?> async : asyncs) {
			if (!async.isDone())
				continue;

			awaitAsyncExecution(async);
		}
	}

	private void checkForAsyncRollbackFailure(List<Future<?>> asyncs) throws ProcessRollbackException {

		for (Future<?> async : asyncs) {
			if (!async.isDone())
				continue;

			awaitAsyncRollback(async);
		}
	}

	private void awaitAsyncExecution(Future<?> async) throws ProcessExecutionException {

		try {
			async.get();
		} catch (ExecutionException ex) {
			// thread returned an exception
			if (ex.getCause() instanceof ProcessExecutionException) {
				throw (ProcessExecutionException) ex.getCause();
			}
		} catch (Exception ex) {
			throw new ProcessExecutionException(this, ex);
		}
	}

	private void awaitAsyncRollback(Future<?> async) throws ProcessRollbackException {

		try {
			async.get();
		} catch (ExecutionException ex) {
			// thread returned an exception
			if (ex.getCause() instanceof ProcessRollbackException) {
				throw (ProcessRollbackException) ex.getCause();
			}
		} catch (Exception ex) {
			throw new ProcessRollbackException(this, ex);
		}
	}
}
