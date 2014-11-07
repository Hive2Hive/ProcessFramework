package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Exception that occurs due to a failure during an {@link IProcessComponent}s rollback.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessRollbackException extends ProcessException {

	private static final long serialVersionUID = -4458285820809151989L;

	/**
	 * Creates a {@code ProcessRollbackException} with the provided source and cause.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessRollbackException} has
	 *            been thrown.
	 * @param cause The cause of this {@code ProcessRollbackException}.
	 */
	public ProcessRollbackException(IProcessComponent<?> source, Throwable cause) {
		this(source, cause, cause.getMessage());
	}

	/**
	 * Creates a {@code ProcessRollbackException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessRollbackException} has
	 *            been thrown.
	 * @param message The detail message of this {@code ProcessRollbackException}.
	 */
	public ProcessRollbackException(IProcessComponent<?> source, String message) {
		this(source, null, message);
	}

	/**
	 * Creates a {@code ProcessRollbackException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessRollbackException} has
	 *            been thrown.
	 * @param cause The cause of this {@code ProcessRollbackException}.
	 * @param message The detail message of this {@code ProcessRollbackException}.
	 */
	public ProcessRollbackException(IProcessComponent<?> source, Throwable cause, String message) {
		super(source, cause, message);
	}

}