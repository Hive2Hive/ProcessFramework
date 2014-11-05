package org.hive2hive.processframework.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.hive2hive.processframework.Process;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.decorators.AsyncComponent;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * A {@link Process} that traverses its components in preorder (i.e., left-to-right).
 * 
 * @author Christian Lüthold
 *
 */
public class PreorderProcess extends Process<Void> {

	private List<IProcessComponent<?>> components = new ArrayList<IProcessComponent<?>>();

	private List<Future<?>> asyncExecutions = new ArrayList<Future<?>>();
	private List<Future<?>> asyncRollbacks = new ArrayList<Future<?>>();

	@Override
	protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {

		// don't use iterator, as component list might be modified during execution
		int executionIndex = 0;
		while (executionIndex < components.size() && getState() == ProcessState.EXECUTING) {

			checkForAsyncExecutionFailure(asyncExecutions);

			IProcessComponent<?> next = components.get(executionIndex);
			if (next instanceof AsyncComponent<?>) {
				Future<?> async = ((AsyncComponent<?>) next).execute();
				asyncExecutions.add(async);
			} else {
				next.execute();
			}
			executionIndex++;
		}

		// await async execution components
		for (Future<?> async : asyncExecutions) {
			awaitAsyncExecution(async);
		}
		asyncExecutions.clear();

		return null;
	}

	@Override
	protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {

		// don't use iterator, as component list might be modified during rollback
		int rollbackIndex = components.size() - 1;

		while (rollbackIndex >= 0 && getState() == ProcessState.ROLLBACKING) {

			checkForAsyncRollbackFailure(asyncRollbacks);

			IProcessComponent<?> last = components.get(rollbackIndex);
			if (last instanceof AsyncComponent<?>) {
				Future<?> async = ((AsyncComponent<?>) last).rollback();
				asyncRollbacks.add(async);
			} else {
				last.rollback();
			}
			rollbackIndex--;
		}

		// await async rollback components
		for (Future<?> async : asyncRollbacks) {
			awaitAsyncRollback(async);
		}
		asyncRollbacks.clear();

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
