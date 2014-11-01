package org.hive2hive.processframework.interfaces;

import java.util.List;

import org.hive2hive.processframework.Process;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;

/**
 * Basic interface for all process components. Defines all common functionalities.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessComponent {

	/**
	 * Starts the execution of this {@code IProcessComponent}.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during a process component's rollback.
	 */
	void start() throws InvalidProcessStateException, ProcessRollbackException;

	/**
	 * Starts the rollback of this {@code IProcessComponent}.
	 * 
	 * @param reason The reason of the cancellation or fail.
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during a process component's rollback.
	 */
	void rollback(RollbackReason reason) throws InvalidProcessStateException, ProcessRollbackException;

	/**
	 * Pauses the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 */
	void pause() throws InvalidProcessStateException;

	/**
	 * Resumes the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this process component is in an invalid state for this
	 *             operation.
	 */
	void resume() throws InvalidProcessStateException;

	/**
	 * Waits for this {@code IProcessComponent} to terminate. Blocks execution until termination.
	 * 
	 * @throws InterruptedException
	 */
	void await() throws InterruptedException;

	/**
	 * Waits for this {@code IProcessComponent} to terminate. Blocks execution until termination or the
	 * provided timeout is reached.
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
	 * Sets the name for this {@code IProcessComponent}.
	 * 
	 * @param name The name for this {@code IProcessComponent}.
	 */
	void setName(String name);

	/**
	 * Gets the name of this {@code IProcessComponent}.
	 * 
	 * @return The name of this {@code IProcessComponent}.
	 */
	String getName();

	/**
	 * Gets the ID of this {@code IProcessComponent}.
	 * 
	 * @return The ID of this {@code IProcessComponent}.
	 */
	String getID();

	/**
	 * Gets the {@link ProcessState} of this {@code IProcessComponent}.
	 * 
	 * @return The {@link ProcessState} of this {@code IProcessComponent}.
	 */
	ProcessState getState();

	/**
	 * Gets the progress of this {@code IProcessComponent}.
	 * 
	 * @return The progress of this {@code IProcessComponent}.
	 */
	double getProgress();

	/**
	 * Gets all {@link IProcessComponentListener}s that are attached to this {@code IProcessComponent}.
	 * 
	 * @return All {@link IProcessComponentListener}s that are attached to this {@code IProcessComponent}.
	 */
	List<IProcessComponentListener> getListeners();
	
	/**
	 * Gets the parent {@link Process} composite of which this {@code IProcessComponent} is a child of.
	 * @return The parent {@link Process} composite of which this {@code IProcessComponent} is a child of.
	 */
	Process getParent();

}
