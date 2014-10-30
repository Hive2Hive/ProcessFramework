package org.hive2hive.processframework.interfaces;

/**
 * Interface for process components that deliver a result.
 * 
 * @author Christian Lüthold
 * 
 * @param <T> The type of the result object.
 */
public interface IResultProcessComponent<T> extends IProcessComponent {

	// TODO controversy about whether this should be public, ProcessComponent notifications are private and
	// notification happens through the attached listeners
	void notifyResultComputed(T result);

	/**
	 * Gets the result computed by this {@code IResultProcessComponent}.
	 * 
	 * @return The computed result or <code>null</code> if the computation has not yet finished.
	 */
	T getResult();

	/**
	 * Attaches an {@link IProcessResultListener} to this {@code IResultProcessComponent}.
	 * 
	 * @param listener The {@link IProcessResultListener} to be attached.
	 */
	void attachListener(IProcessResultListener<T> listener);

	/**
	 * Detaches an {@link IProcessResultListener} to this {@code IResultProcessComponent}.
	 * 
	 * @param listener The {@link IProcessResultListener} to be detached.
	 */
	void detachListener(IProcessResultListener<T> listener);

}