package org.hive2hive.processframework.decorators;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.hive2hive.processframework.FailureReason;
import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO document
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
		
		ExecutionCaller<T> execution = new ExecutionCaller<T>(this, decoratedComponent);
		
		try {
			handle = executor.submit(execution);
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
		
		RollbackCaller rollback = new RollbackCaller(this, decoratedComponent);
		
		try {
			executor.submit(rollback);
		} catch (RejectedExecutionException ex) {
			throw new ProcessRollbackException(new FailureReason(this, ex));
		}
		
		// TODO rollback exceptions are not catched and forwarded
		
		// immediate return, since rollback is async
		return;
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

	private void nameThread(boolean isExecution) {
		try {
			Thread.currentThread().checkAccess();
			Thread.currentThread().setName(String.format("async %s", isExecution ? "execution" : "rollback"));
		} catch (SecurityException ex) {
			logger.warn("The created thread cannot be accessed or renamed.", ex);
		};
	}
	
	private class ExecutionCaller<U> implements Callable<U> {

		private final AsyncComponent<U> decorator;
		private final IProcessComponent<U> component;
		
		public ExecutionCaller(AsyncComponent<U> decorator, IProcessComponent<U> component) {
			this.decorator = decorator;
			this.component = component;
		}
		
		@Override
		public U call() throws Exception {
			
			if (isExecuting && !isRollbacking) {
				nameThread(true);
				try {
					return component.execute();
					
				} catch(InvalidProcessStateException | ProcessExecutionException ex) {
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
			
			if (isRollbacking && !isExecuting) {
				nameThread(false);
				
				// mind: async component might be in any state
				// 1st try
				try {
					component.rollback();
				} catch(InvalidProcessStateException ex) {
					// ProcessComponent.rollback() was allowed, thus AsyncComponent is EXECUTION_SUCCESSED/FAILED or PAUSED
					// -> wrapped component is either EXECUTING, EXECUTION_SUCCESSED/FAILED or PAUSED
					// -> only invalid state for component rollback is EXECUTING
					// -> await execution termination, then start 2nd try
					
					// await execution termination
					try {
						handle.get();
					} catch (ExecutionException ex2) {
						if (ex2.getCause() instanceof ProcessExecutionException) {
							// component execution failed, rollback already triggered, thus rollback
						} else {
							throw ex2;
						}
					}
					
					// 2nd try
					try {
						component.rollback();
					} catch (ProcessRollbackException ex2) {
						throw ex;
					}
				} catch(ProcessRollbackException ex) {
					throw ex;
				}
			} else {
				throw new ProcessExecutionException(new FailureReason(decorator, "Invalid state."));
			}
			
			return null;
		}
	}
}
