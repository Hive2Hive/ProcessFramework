package org.hive2hive.processframework.decorators;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;
import org.hive2hive.processframework.processes.PreorderProcess;

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
 * 
 */
public class AsyncComponent extends ProcessDecorator implements Callable<RollbackReason> {

	private final ExecutorService asyncExecutor;
	private Future<RollbackReason> handle;

	private boolean componentSucceeded = false;
	private boolean componentFailed = false;
	private RollbackReason result = null;

	public AsyncComponent(ProcessComponent decoratedComponent) {
		super(decoratedComponent);

		asyncExecutor = Executors.newSingleThreadExecutor();
	}

	@Override
	protected void doExecute() throws InvalidProcessStateException {

		handle = asyncExecutor.submit(this);
		// immediate return, since execution is async
	}

	@Override
	public RollbackReason call() throws Exception {

		try {
			Thread.currentThread().checkAccess();
			Thread.currentThread().setName("async proc");
		} catch (SecurityException e) {
			// logger.error("Async thread cannot be renamed.", e);
		}
		;

		// starts and rolls back itself if needed (component knows nothing about the composite of which the
		// AsyncComponent is part of)

		decoratedComponent.attachListener(new IProcessComponentListener() {

			@Override
			public void onSucceeded() {
				componentSucceeded = true;
				componentFailed = false;
				result = null;

				//succeed();
			}

			@Override
			public void onFailed(RollbackReason reason) {
				componentSucceeded = false;
				componentFailed = true;
				result = reason;

				// TODO this is suspicious, see https://github.com/Hive2Hive/ProcessFramework/issues/8
				if (getParent() == null) {
					try {
						cancel(reason);
					} catch (InvalidProcessStateException ex) {
						// logger.error("Asynchronous component could not be cancelled.", e);
					} catch (ProcessRollbackException ex) {
						// logger.error("Asynchronous component could not be rolled back.", e);
					}
				}
			}
		});

		// TODO catch the exceptions and forward them!!
		// sync execution
		decoratedComponent.start();

		return result;
	}

	@Override
	protected void doRollback(RollbackReason reason) throws InvalidProcessStateException,
			ProcessRollbackException {
		// mind: async component might be in any state!
	
		try {
			decoratedComponent.cancel(reason);
		} catch (InvalidProcessStateException e) {
			if (e.getInvalidState() == ProcessState.FAILED) {
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

	/*@Override
	protected void succeed() {
		// AsyncComponent does not succeed until component does
		if (componentSucceeded) {
			super.succeed();
		}
	}

	@Override
	protected void fail(RollbackReason reason) {
		// AsyncComponent does not fail until component does
		if (componentFailed) {
			super.fail(reason);
		}
	}*/
	
	@Override
	public ProcessState getState() {
		// TODO to be discussed
		return super.getState();
	}

	public Future<RollbackReason> getHandle() {
		return handle;
	}

}
