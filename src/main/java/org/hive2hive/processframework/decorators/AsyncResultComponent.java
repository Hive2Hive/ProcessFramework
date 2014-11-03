package org.hive2hive.processframework.decorators;

import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.interfaces.IResultProcessComponentListener;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;

/**
 * Decorator for asynchronous components that intend to return a result.
 * 
 * @author Christian Lüthold
 * 
 * @param <T>
 */
public class AsyncResultComponent<T> extends AsyncComponent implements IResultProcessComponent<T> {

	public AsyncResultComponent(ProcessComponent decoratedComponent) {
		super(decoratedComponent);

		// TODO Chris: find a cleaner way --> update whole framework hierarchy with IResultProcessComponent<T>
		if (!(decoratedComponent instanceof IResultProcessComponent<?>)) {
			throw new IllegalArgumentException(
					"Cannot decorate this component as it does not implement IResultProcessComponent<T>.");
		}
	}

	@Override
	public void attachListener(IResultProcessComponentListener<T> listener) {

		((IResultProcessComponent<T>) decoratedComponent).attachListener(listener);
	}

	@Override
	public void detachListener(IResultProcessComponentListener<T> listener) {
		((IResultProcessComponent<T>) decoratedComponent).detachListener(listener);
	}

	@Override
	public T getResult() {
		return ((IResultProcessComponent<T>) decoratedComponent).getResult();
	}

	@Override
	public void computeResult() {
		// TODO Auto-generated method stub
		
	}
}
