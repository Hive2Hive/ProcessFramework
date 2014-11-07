package org.hive2hive.processframework.exceptions;

import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Exception that occurs if an {@link IProcessComponent} has an invalid {@link ProcessState} to execute a
 * specific operation.</br>
 * <b>Example:</b> An {@link IProcessComponent} cannot be paused if its execution has not yet been started.
 * 
 * @author Christian Lüthold
 *
 */
public class InvalidProcessStateException extends ProcessException {

	private static final long serialVersionUID = -6030582840157298231L;
	
	private final ProcessState invalidState;

	/**
	 * Creates a {@code InvalidProcessStateException} containing information about a process
	 * components' current {@link ProcessState}.
	 * 
	 * @param source The source {@link IProcessComponent} where this {@code InvalidProcessStateException} has
	 *            been thrown.
	 * @param invalidState The {@link IProcessComponent}s' current (invalid) {@link ProcessState}.
	 */
	public InvalidProcessStateException(IProcessComponent<?> source, ProcessState invalidState) {
		super(source, String.format(
				"Operation cannot be called. The process component '%s' currently has an invalid state: %s.",
				source.getName(), invalidState));
		this.invalidState = invalidState;
	}

	/**
	 * Gets the invalid state of the associated source {@link IProcessComponent}.
	 * 
	 * @return The invalid state of the associated source {@link IProcessComponent}.
	 */
	public ProcessState getInvalidState() {
		return invalidState;
	}
}
