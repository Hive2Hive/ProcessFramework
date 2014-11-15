package org.hive2hive.processframework.interfaces;

/**
 * Basic {@link IProcessComponent} listener interface.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessComponentListener {

	/**
	 * Fires when the observed {@link IProcessComponent}'s starts its execution.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onExecuting(IProcessEventArgs args);

	/**
	 * Fires when the observed {@link IProcessComponent}'s starts its rollback.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onRollbacking(IProcessEventArgs args);

	/**
	 * Fires when the observed {@link IProcessComponent}'s gets paused.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onPaused(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link IProcessComponent}'s execution succeeded.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onExecutionSucceeded(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link IProcessComponent}'s execution failed.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onExecutionFailed(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link IProcessComponent}'s rollback succeeded.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onRollbackSucceeded(IProcessEventArgs args);

	/**
	 * Fires if the observed {@link IProcessComponent}'s rollback failed.
	 * 
	 * @param args The {@link IProcessEventArgs} associated with this event.
	 */
	void onRollbackFailed(IProcessEventArgs args);

}