package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.RollbackReason;

/**
 * Exception that occurs during a process component execution in case of a failure. Leads to the cancellation
 * and rollback of the whole process composite.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessExecutionException extends Exception {

	private static final long serialVersionUID = -107686918145129011L;

	private final RollbackReason reason;

	public ProcessExecutionException(Throwable cause) {
		this(new RollbackReason(cause.getMessage(), cause));
	}

	public ProcessExecutionException(String hint) {
		this(new RollbackReason(hint, null));
	}

	public ProcessExecutionException(String hint, Throwable cause) {
		this(new RollbackReason(hint, cause));
	}

	public ProcessExecutionException(RollbackReason reason) {
		this.reason = reason;
	}

	public RollbackReason getRollbackReason() {
		return reason;
	}
}
