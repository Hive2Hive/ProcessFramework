package org.hive2hive.processframework;

import org.hive2hive.processframework.interfaces.IProcessComponentListener;

public class TestProcessComponentListener implements IProcessComponentListener {

	protected boolean hasExecutionSucceeded;
	protected boolean hasExecutionFailed;
	protected boolean hasRollbackSucceeded;
	protected boolean hasRollbackFailed;
	
	@Override
	public void onExecutionSucceeded() {
		hasExecutionSucceeded = true;
	}

	@Override
	public void onExecutionFailed() {
		hasExecutionFailed = true;
	}

	@Override
	public void onRollbackSucceeded() {
		hasRollbackSucceeded = true;
	}

	@Override
	public void onRollbackFailed() {
		hasRollbackFailed = true;
	}

}
