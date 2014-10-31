package org.hive2hive.processframework;

import java.util.Collection;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;

/**
 * Abstract base class for all composite {@link ProcessComponent}s (container).
 * These composites contain other {@link ProcessComponent}s (children).
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class Process extends ProcessComponent {

	/**
	 * Adds a {@link ProcessComponent} to this composite {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process}.
	 */
	public final void add(ProcessComponent component) {
		component.setParent(this);
		doAdd(component);
	};

	/**
	 * Removes a {@link ProcessComponent} from this composite {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this composite {@code Process}.
	 */
	public final void remove(ProcessComponent component) {
		component.setParent(null);
		doRemove(component);
	}

	/**
	 * Gets all {@link ProcessComponent}s that are contained in this composite {@code Process}.
	 * 
	 * @return All {@link ProcessComponent}s that are contained in this composite {@code Process}.
	 */
	public abstract Collection<ProcessComponent> getComponents();

	/**
	 * Gets the {@link ProcessComponent} that is contained at the provided index in this composite
	 * {@code Process}.
	 * 
	 * @return The {@link ProcessComponent} that is contained at the provided index in this composite
	 *         {@code Process}.
	 */
	public abstract ProcessComponent getComponent(int index);

	/**
	 * Template method responsible for the adding of a {@link ProcessComponent} to this composite
	 * {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process}.
	 */
	protected abstract void doAdd(ProcessComponent component);

	/**
	 * Template method responsible for the removing of a {@link ProcessComponent} from this composite
	 * {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this composite {@code Process}.
	 */
	protected abstract void doRemove(ProcessComponent component);
	
	@Override
	protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException,
			ProcessRollbackException {
		// do nothing by default
	}
	
	@Override
	protected void doRollback(RollbackReason reason) throws InvalidProcessStateException,
			ProcessRollbackException {
		// do nothing by default
	}
	
	@Override
	protected void doPause() throws InvalidProcessStateException {
		// do nothing by default
	}
	
	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		// do nothing by default
	}
	
	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		// do nothing by default
	}

}
