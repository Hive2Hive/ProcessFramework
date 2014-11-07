package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Exception that occurs due to a failure during an {@link IProcessComponent}s execution.
 * 
 * @author Christian Lüthold
 * 
 */
public class ProcessExecutionException extends ProcessException {

	private static final long serialVersionUID = -668658226830422439L;

	/**
	 * Creates a {@code ProcessExecutionException} with the provided source and cause.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessExecutionException} has
	 *            been thrown.
	 * @param cause The cause of this {@code ProcessExecutionException}.
	 */
	public ProcessExecutionException(IProcessComponent<?> source, Throwable cause) {
		this(source, cause, cause.getMessage());
	}

	/**
	 * Creates a {@code ProcessExecutionException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessExecutionException} has
	 *            been thrown.
	 * @param message The detail message of this {@code ProcessExecutionException}.
	 */
	public ProcessExecutionException(IProcessComponent<?> source, String message) {
		this(source, null, message);
	}

	/**
	 * Creates a {@code ProcessExecutionException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessExecutionException} has
	 *            been thrown.
	 * @param cause The cause of this {@code ProcessExecutionException}.
	 * @param message The detail message of this {@code ProcessExecutionException}.
	 */
	public ProcessExecutionException(IProcessComponent<?> source, Throwable cause, String message) {
		super(source, cause, message);
	}

}
