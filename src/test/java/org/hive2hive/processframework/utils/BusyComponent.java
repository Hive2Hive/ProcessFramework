package org.hive2hive.processframework.utils;

import org.hive2hive.processframework.ProcessDecorator;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulates some seconds of work during execution and rollback for testing purposes.
 * 
 * @author Christian Lüthold
 *
 */
public class BusyComponent extends ProcessDecorator<Void> {

	private static final Logger logger = LoggerFactory.getLogger(BusyComponent.class);
	private static final int SIMULATED_WORK_DURATION_MS = 2000;
	
	public BusyComponent(IProcessComponent<?> decoratedComponent) {
		super(decoratedComponent);
	}

	@Override
	protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
		decoratedComponent.execute();
		
		simulateWork();
		return null;
	}
	
	@Override
	protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
		decoratedComponent.rollback();
		
		simulateWork();
		return null;
	}
	
	private static void simulateWork() {
		try {
			logger.debug(String.format("Simulating %s ms of work.", SIMULATED_WORK_DURATION_MS));
			Thread.sleep(SIMULATED_WORK_DURATION_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return String.format("Busy[%s]", decoratedComponent.toString());
	}
}
