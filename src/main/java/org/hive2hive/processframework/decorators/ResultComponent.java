package org.hive2hive.processframework.decorators;

import java.util.ArrayList;
import java.util.List;

import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.ProcessEventArgs;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;
import org.hive2hive.processframework.interfaces.IResultProcessComponentListener;

// TODO document
public class ResultComponent<T> extends ProcessDecorator implements IResultProcessComponent<T> {

	protected T result;
	private final IResultProcessComponent<T> resultComponent;
	protected final List<IResultProcessComponentListener<T>> listeners;
	
	public ResultComponent(IResultProcessComponent<T> decoratedComponent) {
		super(decoratedComponent);

		resultComponent = decoratedComponent;
		listeners = new ArrayList<IResultProcessComponentListener<T>>();
	}

	@Override
	protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		
		// TODO try/catch, exception
		resultComponent.computeResult();
		notifyListeners();
	}
	
	@Override
	public synchronized void attachListener(IResultProcessComponentListener<T> listener) {
		this.listeners.add(listener);

		// fire event if it already occurred
		if (result != null) {
			listener.onResultReady(new ProcessEventArgs(this));
		}
	}

	@Override
	public synchronized void detachListener(IResultProcessComponentListener<T> listener) {
		this.listeners.remove(listener);
	}

	@Override
	public T getResult() {
		return result;
	}

	protected void notifyListeners() {
		for (IResultProcessComponentListener<T> listener : this.listeners) {
			listener.onResultReady(new ProcessEventArgs(this));
		}
	}

	@Override
	public void computeResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doPause() throws InvalidProcessStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doResumeExecution() throws InvalidProcessStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doResumeRollback() throws InvalidProcessStateException {
		// TODO Auto-generated method stub
		
	}

}
