package org.hive2hive.processframework.decorators;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProcessDecorator} that executes the wrapped/decorated {@link IProcessComponent} on a separate
 * thread and thus immediately returns the control.
 * Both, execution and rollback run on a separate thread and return a {@link Future} object as the result of
 * the asynchronous computation. Possible exceptions can be retrieved through this {@link Future} object.</br>
 * <b>Note:</b>
 * The {@link IProcessComponent} wrapped/decorated by this {@code AsyncComponent} should be <i>independent</i>
 * of any other components in the process composite because it runs asynchronously.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by the wrapped/decorated {@code IProcessComponent}.
 */
public class AsyncComponent<T> extends ProcessDecorator<Future<T>> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncComponent.class);

	// store a reference to the IProcessComponent<T>, such that we know its type argument T
	private volatile IProcessComponent<T> component;

	private volatile Future<T> executionHandle;
	private volatile Future<T> rollbackHandle;

	private final ExecutorService executor;
	private ExecutionRunner executionRunner;
	private RollbackRunner rollbackRunner;
	
	public AsyncComponent(IProcessComponent<T> decoratedComponent) {
		super(decoratedComponent);

		component = decoratedComponent;

		executor = Executors.newFixedThreadPool(1);
	}

	@Override
	protected Future<T> doExecute() throws InvalidProcessStateException, ProcessExecutionException {

		executionRunner = new ExecutionRunner();
		try {
			executionHandle = executor.submit(executionRunner);
		} catch (RejectedExecutionException ex) {
			throw new ProcessExecutionException(this, ex);
		}

		// immediate return, since execution is async
		return executionHandle;
	}

	@Override
	protected Future<T> doRollback() throws InvalidProcessStateException, ProcessRollbackException {

		rollbackRunner = new RollbackRunner();
		try {
			rollbackHandle = executor.submit(rollbackRunner);
		} catch (RejectedExecutionException ex) {
			throw new ProcessRollbackException(this, ex);
		}

		// immediate return, since rollback is async
		return rollbackHandle;
	}

	@Override
	public String toString() {
		return String.format("Async[%s]", decoratedComponent.toString());
	}

	private void nameThread(boolean isExecution) {
		try {
			Thread.currentThread().checkAccess();
			Thread.currentThread().setName(String.format("async %s", isExecution ? "execution" : "rollback"));
		} catch (SecurityException ex) {
		}
	}

	private class ExecutionRunner implements Callable<T> {

		@Override
		public T call() throws Exception {

			nameThread(true);

			// throw all kinds of exceptions
			return component.execute();
		}
	}

	private class RollbackRunner implements Callable<T> {

		@Override
		public T call() throws Exception {

			nameThread(false);
			
			// mind: async component might be in any state
			// 1st try
			try {
				return component.rollback();
			} catch (InvalidProcessStateException ex) {
				// happens only with >1 threads in the pool:
				// ProcessComponent.rollback() was allowed, thus AsyncComponent is EXECUTION_SUCCESSED/FAILED
				// or PAUSED
				// -> wrapped component is either EXECUTING, EXECUTION_SUCCESSED/FAILED or PAUSED
				// -> only invalid state for component rollback is EXECUTING
				// -> await execution termination, then start 2nd try

				// await execution termination
				try {
					logger.debug("Awaiting execution termination before rollback.");
					executionHandle.get();
				} catch (ExecutionException ex2) {
					if (ex2.getCause() instanceof ProcessExecutionException) {
						// component execution failed, rollback already triggered, thus rollback
					} else {
						throw ex2;
					}
				}

				// 2nd try
				try {
					return component.rollback();
				} catch (ProcessRollbackException ex2) {
					throw ex2;
				}
			} catch (ProcessRollbackException ex) {
				throw ex;
			}
		}
	}
}
