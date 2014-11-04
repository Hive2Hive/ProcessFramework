package org.hive2hive.processframework.decorators;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.hive2hive.processframework.FailureReason;
import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.processes.PreorderProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProcessDecorator} that executes, and if necessary rollbacks, the wrapped {@link IProcessComponent}
 * in an asynchronous manner.</br>
 * <b>Note:</b></br>
 * An asynchronous component is executed in an own thread and therefore independent of all other components in
 * a process composite.</br>
 * If existing, the parent container component of an {@link AsyncComponent} is responsible to await the result
 * of the asynchronous component. Therefore, the usage of {@link PreorderProcess} is highly recommended.
 * </br>
 * In case of a failure within the asynchronous component, it rollbacks itself in its own thread and returns
 * the resulting {@link RollbackReason}. In case the {@link AsyncComponent} needs to be cancelled due to a
 * failure in another place in the whole composite, the wrapped component (if necessary) is rolled back in the
 * detecting thread.
 * 
 * @author Christian Lüthold
 * @param <T>
 * 
 */
public class AsyncComponent<T> extends ProcessDecorator<Future<T>> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncComponent.class);
	
	private final ExecutorService executor;
	private Future<T> handle;

	private volatile boolean isExecuting = false;
	private volatile boolean isRollbacking = false;
	
	// store a reference to the IProcessComponent<T>, such that we know its type argument T
	private IProcessComponent<T> decoratedComponent;
	
	public AsyncComponent(IProcessComponent<T> decoratedComponent) {
		super(decoratedComponent);

		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	protected Future<T> doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		
		isExecuting = true;
		isRollbacking = !isExecuting;
		
		ExecutionCaller<T> caller = new ExecutionCaller<T>(this, decoratedComponent);
		
		try {
			handle = executor.submit(caller);
		} catch (RejectedExecutionException ex) {
			throw new ProcessExecutionException(new FailureReason(this, ex));
		}
		
		// immediate return, since execution is async
		return handle;
	}
	
	@Override
	protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		// mind: async component might be in any state!
		
		isRollbacking = true;
		isExecuting = !isRollbacking;
		
		RollbackCaller caller = new RollbackCaller(this, decoratedComponent);
		
		try {
			decoratedComponent.rollback();
		} catch (InvalidProcessStateException e) {
			if (e.getInvalidState() == ProcessState.ROLLBACK_SUCCEEDED
					|| e.getInvalidState() == ProcessState.ROLLBACK_FAILED) {
				// async component rolled itself back already
				return;
			} else {
				throw e;
			}
		}
	}

	@Override
	protected void doPause() throws InvalidProcessStateException {
		// mind: async component might be in any state!
		decoratedComponent.pause();
	}

	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		// mind: async component might be in any state!
		decoratedComponent.resume();
	}

	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		// mind: async component might be in any state!
		decoratedComponent.resume();
	}

	/*
	 * @Override
	 * protected void succeed() {
	 * // AsyncComponent does not succeed until component does
	 * if (componentSucceeded) {
	 * super.succeed();
	 * }
	 * }
	 * 
	 * @Override
	 * protected void fail(RollbackReason reason) {
	 * // AsyncComponent does not fail until component does
	 * if (componentFailed) {
	 * super.fail(reason);
	 * }
	 * }
	 */
	
	private class ExecutionCaller<U> implements Callable<U> {

		private final AsyncComponent<U> decorator;
		private final IProcessComponent<U> component;
		
		public ExecutionCaller(AsyncComponent<U> decorator, IProcessComponent<U> component) {
			this.decorator = decorator;
			this.component = component;
		}
		
		@Override
		public U call() throws Exception {
			
			try {
				Thread.currentThread().checkAccess();
				Thread.currentThread().setName("async execution");
			} catch (SecurityException ex) {
				logger.warn("Execution: The created thread cannot be accessed or renamed.", ex);
			};

			if (isExecuting && !isRollbacking) {
				
				try {
					return component.execute();
					
				} catch(ProcessException ex) {
					throw ex;
				}
			} else {
				throw new ProcessExecutionException(new FailureReason(decorator, "Invalid state."));
			}
		}
	}
	
	private class RollbackCaller implements Callable<Void> {

		private final AsyncComponent<?> decorator;
		private final IProcessComponent<?> component;
		
		public RollbackCaller(AsyncComponent<?> decorator, IProcessComponent<?> component) {
			this.decorator = decorator;
			this.component = component;
		}
		
		@Override
		public Void call() throws Exception {
			
			try {
				Thread.currentThread().checkAccess();
				Thread.currentThread().setName("async rollback");
			} catch (SecurityException ex) {
				logger.warn("Rollback: The created thread cannot be accessed or renamed.", ex);
			};

			if (isRollbacking && !isExecuting) {
				
				try {
					component.execute();
					return null;
					
				} catch(ProcessException ex) {
					throw ex;
				}
			} else {
				throw new ProcessExecutionException(new FailureReason(decorator, "Invalid state."));
			}
		}
	}
}
