package org.hive2hive.processframework;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test tests the {@link ProcessState} transitions.
 * For this, a dummy process component is used that actually does nothing. However, to test the transitions
 * from a specific starting state, we need to set this state by using reflection (see
 * {@link ProcessStateTest#setState(IProcessComponent, ProcessState)}).
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
	public void testReadyStateTransition() throws ProcessRollbackException {

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

	@Test
	public void testExecutingState() throws ProcessRollbackException {

		IProcessComponent comp = TestUtil.sampleComponent();

		// use reflection to set internal process state
		setState(comp, ProcessState.EXECUTING);
		assertTrue(comp.getState() == ProcessState.EXECUTING);

		// test invalid operations
		try {
			comp.start();
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

		// test valid operations
		try {
			comp.pause();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		setState(comp, ProcessState.EXECUTING);
		try {
			comp.cancel(TestUtil.sampleRollbackReason());
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
	}

	@Test
	public void testPausedState() throws ProcessRollbackException {

		IProcessComponent comp = TestUtil.sampleComponent();

		// use reflection to set internal process state
		setState(comp, ProcessState.PAUSED);
		assertTrue(comp.getState() == ProcessState.PAUSED);

		// test invalid operations
		try {
			comp.start();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
		try {
			comp.pause();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}

		// test valid operations
		try {
			comp.resume();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
		setState(comp, ProcessState.PAUSED);
		try {
			comp.cancel(TestUtil.sampleRollbackReason());
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
	}

	@Test
	public void testRollbackingState() throws ProcessRollbackException {

		IProcessComponent comp = TestUtil.sampleComponent();

		// use reflection to set internal process state
		setState(comp, ProcessState.ROLLBACKING);
		assertTrue(comp.getState() == ProcessState.ROLLBACKING);

		// test invalid operations
		try {
			comp.start();
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

		// test valid operations
		try {
			comp.pause();
		} catch (InvalidProcessStateException ex) {
			fail("This operation should have been allowed.");
		}
	}
	
	@Test
	public void testSucceededState() throws ProcessRollbackException {

		IProcessComponent comp = TestUtil.sampleComponent();

		// use reflection to set internal process state
		setState(comp, ProcessState.SUCCEEDED);
		assertTrue(comp.getState() == ProcessState.SUCCEEDED);

		// test invalid operations
		try {
			comp.start();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
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
	}
	
	@Test
	public void testFailedState() throws ProcessRollbackException {

		IProcessComponent comp = TestUtil.sampleComponent();

		// use reflection to set internal process state
		setState(comp, ProcessState.FAILED);
		assertTrue(comp.getState() == ProcessState.FAILED);

		// test invalid operations
		try {
			comp.start();
			fail("InvalidProcessStateException should have been thrown.");
		} catch (InvalidProcessStateException ex) {
			// should happen
		}
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
	}
	
	/**
	 * Uses reflection to set the internal state of a process component.
	 * 
	 * @param object The object the state should be set on.
	 * @param state The state to be set.
	 */
	private void setState(IProcessComponent object, ProcessState state) {
		try {
			Method method = ProcessComponent.class.getDeclaredMethod("setState", ProcessState.class);
			method.setAccessible(true);
			method.invoke(object, state);
		} catch (Exception ex) {
			fail("Reflection failed.");
		}
	}
}
