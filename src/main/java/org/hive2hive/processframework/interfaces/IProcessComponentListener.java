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
	 * 
	 * @param The {@link IProcessEventArgs} associated with this event.
	 */
	void onExecutionSucceeded(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link ProcessComponent}'s execution failed.
	 * 
	 * @param The {@link IProcessEventArgs} associated with this event.
	 */
	void onExecutionFailed(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link ProcessComponent}'s rollback succeeded.
	 * 
	 * @param The {@link IProcessEventArgs} associated with this event.
	 */
	void onRollbackSucceeded(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link ProcessComponent}'s rollback failed.
	 * 
	 * @param The {@link IProcessEventArgs} associated with this event.
	 */
	void onRollbackFailed(IProcessEventArgs args);

}