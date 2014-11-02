package org.hive2hive.processframework.exceptions;


/**
 * Exception that occurs due to a failure during a process component's rollback.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessRollbackException extends Exception {

	private static final long serialVersionUID = -5082116677429110685L;

	// TODO make this exception class similar to the ProcessExecutionException, see
	// https://github.com/Hive2Hive/ProcessFramework/issues/9
	
	public ProcessRollbackException(String hint) {
		super(hint);
	}
}