package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.FailureReason;

/**
 * Abstract base class for all exceptions in the process framework.
 * Contains an instance of {@link FailureReason} for detailed failure information.
 * 
 * @author Christian Lüthold
 *
 */
public abstract class ProcessException extends Exception {

	private static final long serialVersionUID = 5015494786811664550L;

	private final FailureReason reason;

	/**
	 * Creates a {@code ProcessException} with the provided {@link FailureReason}.
	 * 
	 * @param reason The {@link FailureReason} that shall be associated with this {@code ProcessException}.
	 */
	public ProcessException(FailureReason reason) {
		this.reason = reason;
	}

	/**
	 * Gets the {@link FailureReason} associated with this {@code ProcessException}.
	 * 
	 * @return The {@link FailureReason} associated with this {@code ProcessException}.
	 */
	public FailureReason getFailureReason() {
		return reason;
	}
}
