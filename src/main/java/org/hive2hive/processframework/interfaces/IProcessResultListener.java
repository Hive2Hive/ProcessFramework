package org.hive2hive.processframework.interfaces;

/**
 * Listener interface for process components that deliver a result.
 * 
 * @author Christian Lüthold
 * 
 * @param <T> The type of the result object.
 */
public interface IProcessResultListener<T> {

	/**
	 * Fires if the observed process component has computed the result.
	 */
	void onResultReady(T result);
}
