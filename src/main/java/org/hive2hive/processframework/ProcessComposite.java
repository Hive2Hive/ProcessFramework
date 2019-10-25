package org.hive2hive.processframework;

import java.util.Collection;

import org.hive2hive.processframework.interfaces.IProcessComponent;

/**
 * Abstract base class for all composite {@link IProcessComponent}s.
 * These composites contain other {@link IProcessComponent}s.
 * 
 * @author Christian Lüthold
 *
 * @param <T> The type of the result computed by this {@code ProcessComposite}.
 */
public abstract class ProcessComposite<T> extends ProcessComponent<T> {

	protected ProcessComposite() {
		// composites should always require rollback
		setRequiresRollback(true);
	}

	/**
	 * Adds an {@link IProcessComponent} to this {@code ProcessComposite}.
	 * 
	 * @param component The {@link IProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	public final void add(IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(component);
	};

	/**
	 * Adds an {@link IProcessComponent} to this {@code ProcessComposite} at the provided index.
	 * 
	 * @param index The index where to add the {@link IProcessComponent}.
	 * @param component The {@link IProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	public final void add(int index, IProcessComponent<?> component) {
		component.setParent(this);
		doAdd(index, component);
	}

	/**
	 * Inserts an {@link IProcessComponent} directly after another, predecessing {@link IProcessComponent}.
	 * 
	 * @param component The {@link IProcessComponent} to be inserted.
	 * @param predecessor The predecessing {@link IProcessComponent}.
	 * 
	 * @return True, if <b>predecessor</b> could be found and <b>component</b> could be inserted.
	 */
	public final boolean insertAfter(IProcessComponent<?> component, IProcessComponent<?> predecessor) {
		component.setParent(this);
		return doInsertAfter(component, predecessor);
	}

	/**
	 * Removes an {@link IProcessComponent} from this {@code ProcessComposite}.
	 * 
	 * @param component The {@link IProcessComponent} to be removed from this {@code ProcessComposite}.
	 */
	public final void remove(IProcessComponent<?> component) {
		component.setParent(null);
		doRemove(component);
	}

	/**
	 * Gets all {@link IProcessComponent}s that are contained in this {@code ProcessComposite}.
	 * 
	 * @return All {@link IProcessComponent}s that are contained in this {@code ProcessComposite}.
	 */
	public abstract Collection<IProcessComponent<?>> getComponents();

	/**
	 * Gets the {@link IProcessComponent} that is contained at the provided index in this
	 * {@code ProcessComposite}.
	 * 
	 * @param index the index of the component to look for
	 * @return The {@link IProcessComponent} that is contained at the provided index in this
	 *         {@code ProcessComposite}.
	 */
	public abstract IProcessComponent<?> getComponent(int index);

	/**
	 * Template method responsible for the adding of an {@link IProcessComponent} to this
	 * {@code ProcessComposite}.
	 * 
	 * @param component The {@link IProcessComponent} to be added to this {@code ProcessComposite}.
	 */
	protected abstract void doAdd(IProcessComponent<?> component);

	/**
	 * Template method responsible for the adding of an {@link IProcessComponent} to this
	 * {@code ProcessComposite} at the provided index.
	 * 
	 * @param index The index where to add the {@link IProcessComponent}.
	 * @param component The {@link IProcessComponent} to be added to this {@code ProcessComposite} a the
	 *            provided index.
	 */
	protected abstract void doAdd(int index, IProcessComponent<?> component);

	/**
	 * Template method responsible for the insertion of an {@link IProcessComponent} directly after another,
	 * predecessing {@link IProcessComponent}.
	 * 
	 * @param component The {@link IProcessComponent} to be inserted.
	 * @param predecessor The predecessing {@link IProcessComponent}.
	 * 
	 * @return True, if <b>predecessor</b> could be found and <b>component</b> could be inserted.
	 */
	protected abstract boolean doInsertAfter(IProcessComponent<?> component, IProcessComponent<?> predecessor);

	/**
	 * Template method responsible for the removing of a {@link IProcessComponent} from this
	 * {@code ProcessComposite}.
	 * 
	 * @param component The {@link IProcessComponent} to be removed from this {@code ProcessComposite}.
	 */
	protected abstract void doRemove(IProcessComponent<?> component);

}
