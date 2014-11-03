package org.hive2hive.processframework;

import java.util.Collection;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Abstract base class for all composite {@link ProcessComponent}s (container).
 * These composites contain other {@link ProcessComponent}s (children).
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class Process<T> extends ProcessComponent<T> {

	public Process() {
		// composites should always require rollback
		setRequiresRollback(true);
	}

	/**
	 * Adds a {@link ProcessComponent} to this composite {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process}.
	 */
	public final void add(IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(component);
	};

	/**
	 * Adds a {@link ProcessComponent} to this composite {@code Process} at the provided index.
	 * 
	 * @param index The index where to add the {@link ProcessComponent}.
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process}.
	 */
	public final void add(int index, IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(index, component);
	}

	/**
	 * Removes a {@link ProcessComponent} from this composite {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this composite {@code Process}.
	 */
	public final void remove(IProcessComponent<?> component) {
		component.setParent(null);
		doRemove(component);
	}

	/**
	 * Gets all {@link ProcessComponent}s that are contained in this composite {@code Process}.
	 * 
	 * @return All {@link ProcessComponent}s that are contained in this composite {@code Process}.
	 */
	public abstract Collection<IProcessComponent<?>> getComponents();

	/**
	 * Gets the {@link ProcessComponent} that is contained at the provided index in this composite
	 * {@code Process}.
	 * 
	 * @return The {@link ProcessComponent} that is contained at the provided index in this composite
	 *         {@code Process}.
	 */
	public abstract IProcessComponent<?> getComponent(int index);

	/**
	 * Template method responsible for the adding of a {@link ProcessComponent} to this composite
	 * {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process}.
	 */
	protected abstract void doAdd(IProcessComponent<?> component);

	/**
	 * Template method responsible for the adding of a {@link ProcessComponent} to this composite
	 * {@code Process} at the provided index.
	 * 
	 * @param index The index where to add the {@link ProcessComponent}.
	 * @param component The {@link ProcessComponent} to be added to this composite {@code Process} a the
	 *            provided index.
	 */
	protected abstract void doAdd(int index, IProcessComponent<?> component);

	/**
	 * Template method responsible for the removing of a {@link ProcessComponent} from this composite
	 * {@code Process}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this composite {@code Process}.
	 */
	protected abstract void doRemove(IProcessComponent<?> component);

	@Override
	protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
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
