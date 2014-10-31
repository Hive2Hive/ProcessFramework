package org.hive2hive.processframework;

import static org.junit.Assert.*;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessStateTest extends BaseTest {

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
	public void testReadyStateTransition() {
		
		IProcessComponent comp = TestUtil.sampleComponent();
		
		assertTrue(comp.getState() == ProcessState.READY);
		
		// test invalid operations
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		try {
			comp.cancel(TestUtil.sampleRollbackReason());
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		
		// test valid operation
		try {
			comp.start();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
	}
	
	private void checkInvalidOperation() {
		
		
		
	}
}
