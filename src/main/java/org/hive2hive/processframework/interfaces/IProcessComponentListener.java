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
	 * Executed if the observed process component succeeded.
	 */
	void onSucceeded();

	/**
	 * Executed if the observed process component failed.
	 */
	void onFailed(RollbackReason reason);

}