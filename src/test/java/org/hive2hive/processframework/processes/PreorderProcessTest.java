package org.hive2hive.processframework.processes;

import org.hive2hive.processframework.BaseTest;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.ProcessStateTest;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class PreorderProcessTest extends BaseTest {

	@BeforeClass
	public static void initTest() throws Exception {
		testClass = ProcessStateTest.class;
		beforeClass();
	}

	@AfterClass
	public static void endTest() {
		afterClass();
	}
	
	@Test
	public void testInstantiation() {
		
		PreorderProcess pp = new PreorderProcess();
		assertTrue(pp.getState() == ProcessState.READY);
	}
	
	@Test
	public void testExecutionSuccess() {
		
		PreorderProcess proc = TestUtil.executionSuccessPreorderProcess();
		
		try {
			proc.execute();
		} catch (InvalidProcessStateException ex) {
			fail();
		} catch (ProcessExecutionException ex) {
			fail("Should execute successfully.");
		}
		
		assertTrue(proc.getState() == ProcessState.EXECUTION_SUCCEEDED);
	}
	
	@Test
	public void testExecutionFail() {
		
		PreorderProcess proc = TestUtil.executionFailPreorderProcess();
		
		try {
			proc.execute();
			fail("ProcessExecutionException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			fail();
		} catch (ProcessExecutionException ex) {
			// expected
		}
		
		assertTrue(proc.getState() == ProcessState.EXECUTION_FAILED);
	}
	
	@Test
	public void testRollbackSuccess() throws InvalidProcessStateException, ProcessExecutionException {
		
		PreorderProcess proc = TestUtil.rollbackSuccessPreorderProcess();
		proc.execute();
		
		try {
			proc.rollback();
		} catch (ProcessRollbackException ex) {
			fail("Should execute successfully.");
		}
		
		assertTrue(proc.getState() == ProcessState.ROLLBACK_SUCCEEDED);
	}
	
	@Test
	public void testRollbackFail() throws InvalidProcessStateException, ProcessExecutionException {
		
		PreorderProcess proc = TestUtil.rollbackFailPreorderProcess();
		proc.execute();
		
		try {
			proc.rollback();
			fail("ProcessRollbackException should have been thrown.");
		} catch (ProcessRollbackException ex) {
			// expected
		}
		
		assertTrue(proc.getState() == ProcessState.ROLLBACK_FAILED);
	}
}
