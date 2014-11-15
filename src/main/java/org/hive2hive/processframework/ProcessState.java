package org.hive2hive.processframework;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Represents the internal state of an {@link IProcessComponent}.
 * 
 * @author Christian Lüthold
 * 
 */
public enum ProcessState {

	/**
	 * Represents an {@link IProcessComponent} that is ready to be executed.
	 */
	READY,
	/**
	 * Represents an {@link IProcessComponent} that is currently being executed.
	 */
	EXECUTING,
	/**
	 * Represents an {@link IProcessComponent} that has executed successfully.
	 */
	EXECUTION_SUCCEEDED,
	/**
	 * Represents an {@link IProcessComponent} that has executed unsuccessfully due to cancellation or a
	 * failure.
	 */
	EXECUTION_FAILED,
	/**
	 * Represents an {@link IProcessComponent} that is currently being rolled back.
	 */
	ROLLBACKING,
	/**
	 * Represents an {@link IProcessComponent} that has rolled back successfully.
	 */
	ROLLBACK_SUCCEEDED,
	/**
	 * Represents an {@link IProcessComponent} that has rolled back unsuccessfully due to a failure.
	 */
	ROLLBACK_FAILED,
	/**
	 * Represents an {@link IProcessComponent} that is currently paused, whether it is executing or rolling
	 * back.
	 */
	PAUSED
}
