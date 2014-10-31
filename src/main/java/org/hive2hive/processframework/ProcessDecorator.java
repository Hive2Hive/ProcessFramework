package org.hive2hive.processframework;

/**
 * Abstract base class for all decorator {@link ProcessComponent}s.
 * These decorators provide additional behavior or state to existing {@link ProcessComponent}s.
 * 
 * @author Christian Lüthold
 * 
 */
public abstract class ProcessDecorator extends ProcessComponent {

	protected final ProcessComponent decoratedComponent;

	/**
	 * Creates a {@code ProcessDecorator} that wraps/decorates the provided {@link ProcessComponent}.
	 * 
	 * @param decoratedComponent The {@link ProcessComponent} to be wrapped/decorated by this
	 *            {@code ProcessDecorator}.
	 */
	public ProcessDecorator(ProcessComponent decoratedComponent) {
		this.decoratedComponent = decoratedComponent;
	}

}