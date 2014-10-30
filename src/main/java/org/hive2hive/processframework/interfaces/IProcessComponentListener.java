package org.hive2hive.processframework.interfaces;

import org.hive2hive.processframework.RollbackReason;

/**
 * Basic process component listener interface.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessComponentListener {

	/**
	 * Fires if the observed process component has succeeded.
	 */
	void onSucceeded();

	/**
	 * Fires if the observed process component has failed.
	 */
	void onFailed(RollbackReason reason);

}