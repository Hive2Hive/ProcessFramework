package org.hive2hive.processframework;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;

/**
 * Abstract base class for all normal {@link IProcessComponent}s (leaf).
 * These normal components represent a specific operation and do not contain other {@link IProcessComponent}s.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by this {@code ProcessStep}.
 */
public abstract class ProcessStep<T> extends ProcessComponent<T> {

	@Override
	public double getProgress() {

		switch (getState()) {
			case READY:
				return 0.0;
			case EXECUTING:
			case ROLLBACKING:
			case PAUSED:
				return 0.5;
			case EXECUTION_SUCCEEDED:
			case EXECUTION_FAILED:
			case ROLLBACK_SUCCEEDED:
			case ROLLBACK_FAILED:
				return 1.0;
			default:
				return 0.0;
		}
	}

	@Override
	protected T doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		// do nothing by default
		return null;
	}

}
