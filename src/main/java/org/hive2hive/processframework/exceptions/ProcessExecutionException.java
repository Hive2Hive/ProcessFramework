package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.RollbackReason;

/**
 * Exception that occurs due to a failure during a process component's execution. This exception holds a
 * {@link RollbackReason} object that helps exploring the reason of the failure.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessExecutionException extends Exception {

	private static final long serialVersionUID = -107686918145129011L;

	private final RollbackReason reason;

	/**
	 * Creates a {@code ProcessExecutionException} that derives a {@link RollbackReason} from the
	 * {@link Throwable} that caused this exception.
	 * 
	 * @param cause The {@link Throwable} that caused this exception.
	 */
	public ProcessExecutionException(Throwable cause) {
		this(new RollbackReason(cause.getMessage(), cause));
	}

	/**
	 * Creates a {@code ProcessExecutionException} that derives a {@link RollbackReason} from the provided
	 * hint message.
	 * 
	 * @param hint A hint about what might have caused this exception.
	 */
	public ProcessExecutionException(String hint) {
		this(new RollbackReason(hint, null));
	}

	/**
	 * Creates a {@code ProcessExecutionException} that derives a {@link RollbackReason} from the
	 * {@link Throwable} that caused this exception and a provided hint message.
	 * 
	 * @param hint A hint about what might have caused this exception.
	 * @param cause The {@link Throwable} that caused this exception.
	 */
	public ProcessExecutionException(String hint, Throwable cause) {
		this(new RollbackReason(hint, cause));
	}

	/**
	 * Creates a {@code ProcessExecutionException} that shall be associated with the provided
	 * {@link RollbackReason}.
	 * 
	 * @param reason The {@link RollbackReason} that shall be associated with this exception.
	 */
	public ProcessExecutionException(RollbackReason reason) {
		this.reason = reason;
	}

	/**
	 * Gets the {@link RollbackReason} associated with this exception.
	 * 
	 * @return The {@link RollbackReason} associated with this exception.
	 */
	public RollbackReason getRollbackReason() {
		return reason;
	}
}
