package org.hive2hive.processframework;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * The reason associated with an {@link IProcessComponent} execution or rollback failure.
 * 
 * @author Christian Lüthold
 *
 */
public class FailureReason {

	private final IProcessComponent<?> source;
	private final Throwable throwable;
	private final String hint;

	public FailureReason(IProcessComponent<?> source, Throwable throwable) {
		this(source, throwable, "");
	}

	public FailureReason(IProcessComponent<?> source, String hint) {
		this(source, null, hint);
	}

	public FailureReason(IProcessComponent<?> source, Throwable throwable, String hint) {
		this.source = source;
		this.throwable = throwable;
		this.hint = hint;
	}

	/**
	 * Gets the {@link IProcessComponent} associated with this {@code FailureReason}.
	 * 
	 * @return The {@link IProcessComponent} associated with this {@code FailureReason}.
	 */
	public IProcessComponent<?> getSource() {
		return this.source;
	}

	/**
	 * Gets the {@link Throwable} associated with this {@code FailureReason}.
	 * 
	 * @return The {@link Throwable} associated with this {@code FailureReason}.
	 */
	public Throwable getThrowable() {
		return this.throwable;
	}

	/**
	 * Gets the hint associated with this {@code FailureReason}.
	 * 
	 * @return The hint associated with this {@code FailureReason}.
	 */
	public String getHint() {
		return this.hint;
	}
}
