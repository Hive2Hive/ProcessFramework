package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.ProcessState;

/**
 * Exception that occurs if a process component is called for a specific operation but has an invalid
 * {@link ProcessState} to do so.</br>
 * <b>Example:</b> A process component cannot be paused if it has not yet been started.
 * 
 * @author Christian Lüthold
 *
 */
public class InvalidProcessStateException extends ProcessException {

	private static final long serialVersionUID = -570684360354374306L;

	private final ProcessState invalidState;

	/**
	 * Creates a {@code InvalidProcessStateException} containing information about a process
	 * components' current {@link ProcessState}.
	 * 
	 * @param invalidState The process components' current (invalid) {@link ProcessState}.
	 */
	public InvalidProcessStateException(ProcessState invalidState) {
		super(String.format(
				"Operation cannot be called. The process component currently has an invalid state: %s.",
				invalidState));
		this.invalidState = invalidState;
	}

	/**
	 * Gets the invalid state of the process component.
	 * 
	 * @return The invalid state of the process component.
	 */
	public ProcessState getInvalidState() {
		return invalidState;
	}
}
