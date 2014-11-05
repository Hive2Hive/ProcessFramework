package org.hive2hive.processframework;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test tests the {@link ProcessState} transitions.
 * For this, a dummy process component is used that actually does nothing. However, to test the transitions
 * from a specific starting state, we need to set this state by using reflection (see
 * {@link TestUtil#setState(IProcessComponent, ProcessState)}).
 * 
 * @author Christian Lüthold
 *
 */
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
	public void testReadyStateTransition() throws ProcessExecutionException, ProcessRollbackException {

		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		assertTrue(comp.getState() == ProcessState.READY);

		// test valid operation
		TestUtil.setState(comp, ProcessState.READY);
		try {
			comp.execute();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.READY);
		try {
			comp.rollback();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.READY);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.READY);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}

	@Test
	public void testExecutingState() throws ProcessExecutionException, ProcessRollbackException {

		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.EXECUTING);
		assertTrue(comp.getState() == ProcessState.EXECUTING);

		// test valid operations
		TestUtil.setState(comp, ProcessState.EXECUTING);
		try {
			comp.pause();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.EXECUTING);
		try {
			comp.execute();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTING);
		try {
			comp.rollback();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTING);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}

	@Test
	public void testPausedState() throws ProcessExecutionException, ProcessRollbackException {

		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.PAUSED);
		assertTrue(comp.getState() == ProcessState.PAUSED);

		// test valid operations
		TestUtil.setState(comp, ProcessState.PAUSED);
		try {
			comp.execute();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		TestUtil.setState(comp, ProcessState.PAUSED);
		try {
			comp.rollback();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		TestUtil.setState(comp, ProcessState.PAUSED);
		try {
			comp.resume();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.PAUSED);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}

	@Test
	public void testRollbackingState() throws ProcessExecutionException, ProcessRollbackException {

		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.ROLLBACKING);
		assertTrue(comp.getState() == ProcessState.ROLLBACKING);

		// test valid operations
		TestUtil.setState(comp, ProcessState.ROLLBACKING);
		try {
			comp.pause();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.ROLLBACKING);
		try {
			comp.execute();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACKING);
		try {
			comp.rollback();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACKING);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}
	
	@Test
	public void testExecutionSucceededState() throws ProcessExecutionException, ProcessRollbackException {
		
		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.EXECUTION_SUCCEEDED);
		assertTrue(comp.getState() == ProcessState.EXECUTION_SUCCEEDED);

		// test valid operations
		TestUtil.setState(comp, ProcessState.EXECUTION_SUCCEEDED);
		try {
			comp.rollback();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.EXECUTION_SUCCEEDED);
		try {
			comp.execute();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTION_SUCCEEDED);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTION_SUCCEEDED);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}
	
	@Test
	public void testExecutionFailedState() throws ProcessExecutionException, ProcessRollbackException {
		
		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		assertTrue(comp.getState() == ProcessState.EXECUTION_FAILED);

		// test valid operations
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		try {
			comp.rollback();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		try {
			comp.execute();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}
	
	@Test
	public void testRollbackSucceededState() throws ProcessExecutionException, ProcessRollbackException {
		
		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.ROLLBACK_SUCCEEDED);
		assertTrue(comp.getState() == ProcessState.ROLLBACK_SUCCEEDED);

		// test valid operations
		TestUtil.setState(comp, ProcessState.ROLLBACK_SUCCEEDED);
		try {
			comp.execute();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.ROLLBACK_SUCCEEDED);
		try {
			comp.rollback();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACK_SUCCEEDED);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACK_SUCCEEDED);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}
	
	@Test
	public void testRollbackFailedState() throws ProcessExecutionException, ProcessRollbackException {
		
		IProcessComponent<?> comp = TestUtil.executionSuccessComponent();

		// use reflection to set internal process state
		TestUtil.setState(comp, ProcessState.ROLLBACK_FAILED);
		assertTrue(comp.getState() == ProcessState.ROLLBACK_FAILED);

		// test valid operations
		
		// test invalid operations
		TestUtil.setState(comp, ProcessState.ROLLBACK_FAILED);
		try {
			comp.execute();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACK_FAILED);
		try {
			comp.rollback();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACK_FAILED);
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		TestUtil.setState(comp, ProcessState.ROLLBACK_FAILED);
		try {
			comp.resume();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
	}
	
	
}
