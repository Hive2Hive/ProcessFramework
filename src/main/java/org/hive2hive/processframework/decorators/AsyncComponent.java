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
 * the asynchronous computation. Possible exceptions can be retrieved through this {@link Future} object (see
 * example).</br>
 * <b>Note:</b>
 * The {@link IProcessComponent} wrapped/decorated by this {@code AsyncComponent} should be <i>independent</i>
 * of any other components in the process composite because it runs asynchronously.</br></br>
 * <b>Example:</b> Asynchronous process execution:
 * 
 * <pre>
 * AsyncComponent <Integer> asyncComp = new AsyncComponent<>(...);
 * Future<Integer> futureResult;
 * int result;
 * 
 * try {
 *     futureResult = asyncComp.execute();
 * } catch (InvalidProcessStateException ex) {
 *     System.err.println("Invalid state for execution.");
 * } catch (ProcessExecutionException ex) {
 *     System.err.println("AsyncComponent has encountered an error during execution.");
 * }
 * 	
 * // get the result
 * try {
 *     result = futureResult.get();
 * } catch (InterruptedException | CancellationException ex) {
 *     System.err.println("Something went wrong with the thread.");
 * } catch (ExecutionException ex) {
 *     // the thread thew an exception
 *     if (ex.getCause() instanceof ProcessExecutionException) {
 *         System.err.println("Some async process component threw an exception during execution.");
 *     }
 * }
 * </pre>
 * 
 * <b>Example:</b> Asynchronous process rollback works similar, but throws a possible
 * {@link ProcessRollbackException}.
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

	public AsyncComponent(IProcessComponent<T> decoratedComponent) {
		super(decoratedComponent);
		component = decoratedComponent;
	}

	@Override
	protected Future<T> doExecute() throws InvalidProcessStateException, ProcessExecutionException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		ExecutionRunner executionRunner = new ExecutionRunner();
		try {
			executionHandle = executor.submit(executionRunner);
		} catch (RejectedExecutionException ex) {
			throw new ProcessExecutionException(this, ex);
		} finally {
			// already shutdown the executor, does not disturb the already running thread
			executor.shutdown();
		}

		// immediate return, since execution is async
		return executionHandle;
	}

	@Override
	protected Future<T> doRollback() throws InvalidProcessStateException, ProcessRollbackException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		RollbackRunner rollbackRunner = new RollbackRunner();
		try {
			// immediate return, since rollback is async
			return executor.submit(rollbackRunner);
		} catch (RejectedExecutionException ex) {
			throw new ProcessRollbackException(this, ex);
		} finally {
			// already shutdown the executor, does not disturb the already running thread
			executor.shutdown();
		}
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
			// occurs if the current thread does not have access
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
