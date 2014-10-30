package org.hive2hive.processframework.interfaces;

import java.util.List;

import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

/**
 * Basic interface for all process components. Defines all common functionalities.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessComponent {

	/**
	 * Starts this {@code IProcessComponent} and therefore triggers its execution.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this operation.
	 */
	void start() throws InvalidProcessStateException;

	/**
	 * Pauses the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this operation.
	 */
	void pause() throws InvalidProcessStateException;

	/**
	 * Resumes the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this operation.
	 */
	void resume() throws InvalidProcessStateException;

	/**
	 * Cancels the execution of this {@code IProcessComponent} and therefore triggers its rollback.
	 * 
	 * @param reason The reason of the cancellation or fail.
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this operation.
	 */
	void cancel(RollbackReason reason) throws InvalidProcessStateException;

	/**
	 * Waits for this {@code IProcessComponent} to terminate. Blocks execution until termination.
	 * 
	 * @throws InterruptedException
	 */
	void await() throws InterruptedException;

	/**
	 * Waits for this {@code IProcessComponent} to terminate. Blocks execution until termination.
	 * 
	 * @param timeout The maximal waiting time in milliseconds.
	 * @throws InterruptedException
	 */
	void await(long timeout) throws InterruptedException;

	/**
	 * Attaches an {@link IProcessComponentListener} to this {@code IProcessComponent}.
	 * 
	 * @param listener The {@link IProcessComponentListener} to be attached.
	 */
	void attachListener(IProcessComponentListener listener);

	/**
	 * Detaches an {@link IProcessComponentListener} from this {@code IProcessComponent}.
	 * 
	 * @param listener The {@link IProcessComponentListener} to be detached.
	 */
	void detachListener(IProcessComponentListener listener);

	/**
	 * Gets all {@link IProcessComponentListener}s that are attached to this {@code IProcessComponent}.
	 * 
	 * @return All {@link IProcessComponentListener}s that are attached to this {@code IProcessComponent}.
	 */
	List<IProcessComponentListener> getListener();

	/**
	 * Gets the ID of this {@code IProcessComponent}.
	 * 
	 * @return The ID of this {@code IProcessComponent}.
	 */
	String getID();

	/**
	 * Gets the progress of this {@code IProcessComponent}.
	 * 
	 * @return The progress of this {@code IProcessComponent}.
	 */
	double getProgress();

	/**
	 * Gets the {@link ProcessState} of this {@code IProcessComponent}.
	 * 
	 * @return The {@link ProcessState} of this {@code IProcessComponent}.
	 */
	ProcessState getState();

}
