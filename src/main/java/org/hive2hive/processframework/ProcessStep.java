package org.hive2hive.processframework;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;

/**
 * Abstract base class for all (leaf) process components that represent a specific operation and do not
 * contain further components.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessStep extends ProcessComponent {

	@Override
	protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		// do nothing by default
	}

	@Override
	protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		// do nothing by default
	}

	@Override
	protected void doPause() throws InvalidProcessStateException {
		// do nothing by default
	}

	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		// do nothing by default
	}

	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		// do nothing by default
	}
}
