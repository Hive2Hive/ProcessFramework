package org.hive2hive.processframework.interfaces;

import java.util.List;
import java.util.concurrent.Future;

import org.hive2hive.processframework.ProcessComposite;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;

/**
 * Basic interface for all process components. Defines all common functionality.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by this {@code IProcessComponent}.
 */
public interface IProcessComponent<T> {

	/**
	 * Starts the synchronous execution of this {@code IProcessComponent}. Upon successful execution, returns
	 * the computed result of type {@code T}.
	 * 
	 * @return The computed result of type {@code T}.
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure occured during a {@code IProcessComponent}'s execution.
	 */
	T execute() throws InvalidProcessStateException, ProcessExecutionException;

	/**
	 * Starts the asynchronous execution of this {@code IProcessComponent}. Returns a {@link Future} object as
	 * the result of the asynchronous computation of the result.
	 * 
	 * @return The {@code Future} object for the computed result of type {@code T}.
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure occured during a {@code IProcessComponent}'s execution.
	 */
	Future<T> executeAsync() throws InvalidProcessStateException, ProcessExecutionException;

	/**
	 * Starts the synchronous rollback of this {@code IProcessComponent}. Upon successful rollback, returns
	 * the computed result of type {@code T}.
	 * 
	 * @return The computed result of type {@code T}.
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during a {@code IProcessComponent}'s rollback.
	 */
	T rollback() throws InvalidProcessStateException, ProcessRollbackException;

	/**
	 * Starts the asynchronous rollback of this {@code IProcessComponent}. Returns a {@link Future} object as
	 * the result of the asynchronous computation of the result.
	 * 
	 * @return The {@code Future} object for the computed result of type {@code T}.
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessRollbackException If a failure occured during a {@code IProcessComponent}'s rollback.
	 */
	Future<T> rollbackAsync() throws InvalidProcessStateException, ProcessRollbackException;

	/**
	 * Pauses the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 */
	void pause() throws InvalidProcessStateException;

	/**
	 * Resumes the execution or rollback of this {@code IProcessComponent}, depending on its current state.
	 * 
	 * @throws InvalidProcessStateException If this {@code IProcessComponent} is in an invalid state for this
	 *             operation.
	 * @throws ProcessExecutionException If a failure occured during a {@code IProcessComponent}'s execution.
	 * @throws ProcessRollbackException If a failure occured during a {@code IProcessComponent}'s rollback.
	 */
	void resume() throws InvalidProcessStateException, ProcessExecutionException, ProcessRollbackException;

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
	 * Sets the parent for this {@code IProcessComponent}.
	 * 
	 * @param parent The parent for this {@code IProcessComponent}.
	 */
	void setParent(ProcessComposite<?> parent);

	/**
	 * Gets the parent {@link ProcessComposite} composite of which this {@code IProcessComponent} is a child
	 * of.
	 * 
	 * @return The parent {@link ProcessComposite} composite of which this {@code IProcessComponent} is a
	 *         child of.
	 */
	ProcessComposite<?> getParent();

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
	 * Indicates whether this {@code IProcessComponent} requires a rollback.
	 * 
	 * @return True, if this {@code IProcessComponent} requires a rollback, false otherwise.
	 */
	boolean getRollbackRequired();
}
