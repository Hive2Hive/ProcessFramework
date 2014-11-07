package org.hive2hive.processframework;

import java.util.Collection;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Abstract base class for all composite {@link ProcessComponent}s.
 * These composites contain other {@link ProcessComponent}s.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by this {@code ProcessComposite}.
 */
public abstract class ProcessComposite<T> extends ProcessComponent<T> {

	public ProcessComposite() {
		// composites should always require rollback
		setRequiresRollback(true);
	}

	/**
	 * Adds a {@link ProcessComponent} to this {@code ProcessComposite}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	public final void add(IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(component);
	};

	/**
	 * Adds a {@link ProcessComponent} to this {@code ProcessComposite} at the provided index.
	 * 
	 * @param index The index where to add the {@link ProcessComponent}.
	 * @param component The {@link ProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	public final void add(int index, IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(index, component);
	}

	/**
	 * Removes a {@link ProcessComponent} from this {@code ProcessComposite}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this {@code ProcessComposite}.
	 */
	public final void remove(IProcessComponent<?> component) {
		component.setParent(null);
		doRemove(component);
	}

	/**
	 * Gets all {@link ProcessComponent}s that are contained in this {@code ProcessComposite}.
	 * 
	 * @return All {@link ProcessComponent}s that are contained in this {@code ProcessComposite}.
	 */
	public abstract Collection<IProcessComponent<?>> getComponents();

	/**
	 * Gets the {@link ProcessComponent} that is contained at the provided index in this
	 * {@code ProcessComposite}.
	 * 
	 * @return The {@link ProcessComponent} that is contained at the provided index in this
	 *         {@code ProcessComposite}.
	 */
	public abstract IProcessComponent<?> getComponent(int index);

	/**
	 * Template method responsible for the adding of a {@link ProcessComponent} to this
	 * {@code ProcessComposite}.
	 * 
	 * @param component The {@link ProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	protected abstract void doAdd(IProcessComponent<?> component);

	/**
	 * Template method responsible for the adding of a {@link ProcessComponent} to this
	 * {@code ProcessComposite} at the provided index.
	 * 
	 * @param index The index where to add the {@link ProcessComponent}.
	 * @param component The {@link ProcessComponent} to be added to this {@code ProcessComposite} a the
	 *            provided index.
	 */
	protected abstract void doAdd(int index, IProcessComponent<?> component);

	/**
	 * Template method responsible for the removing of a {@link ProcessComponent} from this
	 * {@code ProcessComposite}.
	 * 
	 * @param component The {@link ProcessComponent} to be removed from this {@code ProcessComposite}.
	 */
	protected abstract void doRemove(IProcessComponent<?> component);

}
