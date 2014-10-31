package org.hive2hive.processframework;

/**
 * Represents the internal state of a {@link ProcessComponent}.
 * 
 * @author Christian Lüthold
 * 
 */
public enum ProcessState {

	/**
	 * Represents a {@link ProcessComponent} that is ready to be executed.
	 */
	READY,
	/**
	 * Represents a {@link ProcessComponent} that is currently being executed.
	 */
	EXECUTING,
	/**
	 * Represents a {@link ProcessComponent} that is currently being rolled back.
	 */
	ROLLBACKING,
	/**
	 * Represents a {@link ProcessComponent} that is currently paused, whether it is executing or rolling back.
	 */
	PAUSED,
	/**
	 * Represents a {@link ProcessComponent} that has finished successfully.
	 */
	SUCCEEDED,
	/**
	 * Represents a {@link ProcessComponent} that has finished unsuccessfully due to cancellation or fail.
	 */
	FAILED
}
