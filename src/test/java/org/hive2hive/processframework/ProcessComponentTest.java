package org.hive2hive.processframework;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.utils.BusyComponent;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessComponentTest extends BaseTest {

	@BeforeClass
	public static void initTest() throws Exception {
		testClass = ProcessComponentTest.class;
		beforeClass();
	}

	@AfterClass
	public static void endTest() {
		afterClass();
	}
	
	@Test
	public void testExecute() throws InvalidProcessStateException, ProcessExecutionException {
		
		ProcessComponent<Void> component = TestUtil.executionSuccessComponent(false);
		component.execute();
		
		assertTrue(component.getState() == ProcessState.EXECUTION_SUCCEEDED);
	}
	
	@Test
	public void testExecuteAsync() throws InvalidProcessStateException, ProcessExecutionException {
		
		ProcessComponent<Void> component = TestUtil.executionSuccessComponent(false);
		IProcessComponent<Void> busyComp = new BusyComponent(component);
		
		Future<Void> future = busyComp.executeAsync();
		
		assertTrue(component.getState() != ProcessState.EXECUTION_SUCCEEDED);
		
		try {
			future.get();
		} catch (InterruptedException | ExecutionException ex) {
			fail(ex.getMessage());
		}
		
		assertTrue(component.getState() == ProcessState.EXECUTION_SUCCEEDED);
	}
}
