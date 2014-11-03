package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.FailureReason;
import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Exception that occurs due to a failure during an {@link IProcessComponent}s rollback.
 * Contains an instance of {@link FailureReason} for detailed failure information.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessRollbackException extends ProcessException {

	private static final long serialVersionUID = 4136893949400894677L;

	/**
	 * Creates a {@code ProcessRollbackException} with the provided {@link FailureReason}.
	 * 
	 * @param reason The {@link FailureReason} that shall be associated with this
	 *            {@code ProcessRollbackException}.
	 */
	public ProcessRollbackException(FailureReason reason) {
		super(reason);
	}

}