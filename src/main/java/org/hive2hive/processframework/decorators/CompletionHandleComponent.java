package org.hive2hive.processframework.decorators;

import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.ICompletionHandle;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;

/**
 * A {@link ProcessDecorator} that executes an event, defined in the {@link ICompletionHandle}, after
 * completion of the wrapped {@link ProcessComponent}.
 * 
 * @author Christian Lüthold
 * 
 */
public class CompletionHandleComponent extends ProcessDecorator {

	private final ICompletionHandle handle;

	public CompletionHandleComponent(ProcessComponent decoratedComponent, ICompletionHandle handle) {
		super(decoratedComponent);
		this.handle = handle;
	}

	@Override
	protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		IProcessComponentListener listener = new IProcessComponentListener() {
			public void onSucceeded() {
				handle.onCompletionSuccess();
			}

			public void onFailed(RollbackReason reason) {
				handle.onCompletionFailure(reason);
			}
		};

		decoratedComponent.attachListener(listener);
		decoratedComponent.execute();
	}

	@Override
	protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		decoratedComponent.rollback();
	}

	@Override
	protected void doPause() throws InvalidProcessStateException {
		decoratedComponent.pause();
	}

	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		decoratedComponent.resume();
	}

	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		decoratedComponent.resume();
	}

}
