package org.hive2hive.processframework;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

public class TestUtil {

	/**
	 * Creates an anonymous sample {@link ProcessComponent} for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static IProcessComponent sampleComponent() {

		return new ProcessComponent() {
			
			@Override
			protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				// do nothing
			}

			@Override
			protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
			}
			
			@Override
			protected void doPause() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeExecution() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeRollback() throws InvalidProcessStateException {
				// do nothing
			}
		};
	}
	
	/**
	 * Creates a sample {@link RollbackReason} for testing purposes.
	 * @return A sample {@link RollbackReason} for testing purposes.
	 */
	public static RollbackReason sampleRollbackReason() {
		
		return new RollbackReason("This is a sample rollback reason for testing purposes.");
	}
}
