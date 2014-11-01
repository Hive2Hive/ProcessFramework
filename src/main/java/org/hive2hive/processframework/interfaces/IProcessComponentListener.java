package org.hive2hive.processframework.interfaces;

import org.hive2hive.processframework.ProcessComponent;

/**
 * Basic {@link ProcessComponen} listener interface.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessComponentListener {

	/**
	 * Fires if the observed {@link ProcessComponent}'s execution succeeded.
	 */
	void onExecutionSucceeded();
	
	/**
	 * Fires if the observed {@link ProcessComponent}'s execution failed.
	 */
	void onExecutionFailed();
	
	/**
	 * Fires if the observed {@link ProcessComponent}'s rollback succeeded.
	 */
	void onRollbackSucceeded();
	
	/**
	 * Fires if the observed {@link ProcessComponent}'s rollback failed.
	 */
	void onRollbackFailed();
	
}