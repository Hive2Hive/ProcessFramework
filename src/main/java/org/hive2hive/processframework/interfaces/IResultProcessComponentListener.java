package org.hive2hive.processframework.interfaces;

import org.hive2hive.processframework.decorators.ResultComponent;

/**
 * Basic {@link ResultComponent} listener interface.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IResultProcessComponentListener<T> {

	/**
	 * Fires when the observed {@link ResultComponent}'s has computed the result.
	 * 
	 * @param The {@link IProcessEventArgs} associated with this event.
	 */
	void onResultReady(IProcessEventArgs args);
}
