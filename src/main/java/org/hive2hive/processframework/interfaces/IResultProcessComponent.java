package org.hive2hive.processframework.interfaces;

/**
 * Interface for {@link IProcessComponent}s that deliver a result.
 * 
 * @author Christian Lüthold
 * 
 * @param <T> The type of the result object.
 */
public interface IResultProcessComponent<T> extends IProcessComponent {

	// TODO document
	public void computeResult();
	
	/**
	 * Attaches an {@link IResultProcessComponentListener} to this {@code IResultProcessComponent}.
	 * 
	 * @param listener The {@link IResultProcessComponentListener} to be attached.
	 */
	public void attachListener(IResultProcessComponentListener<T> listener);

	/**
	 * Detaches an {@link IResultProcessComponentListener} from this {@code IResultProcessComponent}.
	 * 
	 * @param listener The {@link IResultProcessComponentListener} to be detached.
	 */
	public void detachListener(IResultProcessComponentListener<T> listener);
	
	/**
	 * Gets the result computed by this {@code IResultProcessComponent}.
	 * 
	 * @return The computed result or <code>null</code> if the computation has not yet finished.
	 */
	public T getResult();

}