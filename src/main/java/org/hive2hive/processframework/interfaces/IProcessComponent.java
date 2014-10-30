package org.hive2hive.processframework.interfaces;

import java.util.List;

import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

/**
 * Basic interface defining all common functionalities and providing the public API for all
 * process components of the framework.
 * 
 * @author Christian L�thold
 * 
 */
public interface IProcessComponent {

	/**
	 * Starts the component and therefore triggers its execution.
	 * 
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	IProcessComponent start() throws InvalidProcessStateException;

	/**
	 * Pauses the execution or rollbacking of the component, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	void pause() throws InvalidProcessStateException;

	/**
	 * Resumes the execution or rollbacking of the component, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	void resume() throws InvalidProcessStateException;

	/**
	 * Cancels the component and therefore triggers its rollback.
	 * 
	 * @param reason The reason of the cancellation or fail.
	 * @throws InvalidProcessStateException If the component is in an invalid state for this operation.
	 */
	void cancel(RollbackReason reason) throws InvalidProcessStateException;
	
	/**
	 * Wait for the process component to terminate. Blocks execution until then.
	 * 
	 * @throws InterruptedException
	 */
	void await() throws InterruptedException;

	/**
	 * Wait for the process component to terminate. Blocks execution until then.
	 * 
	 * @param timeout The maximal waiting time in milliseconds.
	 * @throws InterruptedException
	 */
	void await(long timeout) throws InterruptedException;

	/**
	 * Attaches an {@link IProcessComponentListener} to the process component.
	 * 
	 * @param listener The listener to be attached.
	 */
	void attachListener(IProcessComponentListener listener);

	/**
	 * Detaches an {@link IProcessComponentListener} from the process component.
	 * 
	 * @param listener The listener to be detached.
	 */
	void detachListener(IProcessComponentListener listener);

	/**
	 * Getter for the {@link IProcessComponent}'s {@link IProcessComponentListener}s.
	 * 
	 * @return The {@link IProcessComponentListener}s attached to this {@link IProcessComponent}.
	 */
	List<IProcessComponentListener> getListener();

	/**
	 * Getter for the process component's ID.
	 * 
	 * @return The ID of the process component.
	 */
	String getID();

	/**
	 * Getter for the process component's progress.
	 * 
	 * @return The progress of the process component.
	 */
	double getProgress();

	/**
	 * Getter for the process component's parent.
	 * 
	 * @return The parent of the process component.
	 */
	ProcessState getState();

}
