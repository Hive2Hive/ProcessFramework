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
public class AsyncComponent<T> extends ProcessDecorator<Future<T>> implements Callable<T> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncComponent.class);
	
	private final ExecutorService executor;
	
	private Future<T> executionHandle;
	private Future<T> rollbackHandle;

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
		
		try {
			executionHandle = executor.submit(this);
		} catch (RejectedExecutionException ex) {
			throw new ProcessExecutionException(new FailureReason(this, ex));
		}
		
		// immediate return, since execution is async
		return executionHandle;
	}
	
	@Override
	protected Future<T> doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		
		isRollbacking = true;
		isExecuting = !isRollbacking;
		
		try {
			rollbackHandle = executor.submit(this);
		} catch (RejectedExecutionException ex) {
			throw new ProcessRollbackException(new FailureReason(this, ex));
		}
		
		// immediate return, since rollback is async
		return rollbackHandle;
	}

	@Override
	public T call() throws Exception {
		
		if (isExecuting && !isRollbacking) {
			
			// execution
			nameThread(true);
			try {
				return decoratedComponent.execute();
				
			} catch(InvalidProcessStateException | ProcessExecutionException ex) {
				throw ex;
			}
		} else if(isRollbacking && !isExecuting) {
			
			// rollback
			nameThread(false);
			// mind: async component might be in any state
			// 1st try
			try {
				return decoratedComponent.rollback();
			} catch(InvalidProcessStateException ex) {
				// ProcessComponent.rollback() was allowed, thus AsyncComponent is EXECUTION_SUCCESSED/FAILED or PAUSED
				// -> wrapped component is either EXECUTING, EXECUTION_SUCCESSED/FAILED or PAUSED
				// -> only invalid state for component rollback is EXECUTING
				// -> await execution termination, then start 2nd try
				
				// await execution termination
				try {
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
					return decoratedComponent.rollback();
				} catch (ProcessRollbackException ex2) {
					throw ex;
				}
			} catch(ProcessRollbackException ex) {
				throw ex;
			}
		} else {
			throw new Exception("Invalid state.");
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

	private void nameThread(boolean isExecution) {
		try {
			Thread.currentThread().checkAccess();
			Thread.currentThread().setName(String.format("async %s", isExecution ? "execution" : "rollback"));
		} catch (SecurityException ex) {
			logger.warn("The created thread cannot be accessed or renamed.", ex);
		};
	}
}
