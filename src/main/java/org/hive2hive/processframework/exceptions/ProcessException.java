package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Abstract base class for all exceptions in the process framework.
 * Holds a reference to the source {@link IProcessComponent} that threw this {@code ProcessException}.
 * 
 * @author Christian Lüthold
 *
 */
public abstract class ProcessException extends Exception {

	private static final long serialVersionUID = 6488674673099898831L;
	
	private final IProcessComponent<?> source;

	/**
	 * Creates a {@code ProcessException} with the provided source and cause.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessException} has been thrown.
	 * @param cause The cause of this {@code ProcessException}.
	 */
	public ProcessException(IProcessComponent<?> source, Throwable cause) {
		this(source, cause, cause.getMessage());
	}

	/**
	 * Creates a {@code ProcessException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessException} has been thrown.
	 * @param message The detail message of this {@code ProcessException}.
	 */
	public ProcessException(IProcessComponent<?> source, String message) {
		this(source, null, message);
	}

	/**
	 * Creates a {@code ProcessException} with the provided source and message.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code ProcessException} has been thrown.
	 * @param cause The cause of this {@code ProcessException}.
	 * @param message The detail message of this {@code ProcessException}.
	 */
	public ProcessException(IProcessComponent<?> source, Throwable cause, String message) {
		super(message, cause);
		this.source = source;
	}

	/**
	 * Gets the {@link IProcessComponent} that is the source of this {@code ProcessException}.
	 * 
	 * @return The {@link IProcessComponent} that is the source of this {@code ProcessException}.
	 */
	public IProcessComponent<?> getSource() {
		return this.source;
	}
}
