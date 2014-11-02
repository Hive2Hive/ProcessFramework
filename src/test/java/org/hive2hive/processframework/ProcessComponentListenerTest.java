package org.hive2hive.processframework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.utils.TestProcessComponentListener;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessComponentListenerTest extends BaseTest {

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

		TestProcessComponentListener listener = new TestProcessComponentListener();
		assertFalse(listener.hasExecutionSucceeded());
		assertFalse(listener.hasExecutionFailed());
		assertFalse(listener.hasRollbackSucceeded());
		assertFalse(listener.hasRollbackFailed());
	}

	@Test
	public void testOnExecutionSuceeded() throws InvalidProcessStateException, ProcessExecutionException {

		TestProcessComponentListener listener = new TestProcessComponentListener();

		IProcessComponent comp = TestUtil.executionSuccessComponent();
		comp.attachListener(listener);

		comp.execute();

		assertTrue(listener.hasExecutionSucceeded());
		assertFalse(listener.hasExecutionFailed());
	}

	@Test
	public void testOnExecutionFailed() throws InvalidProcessStateException {

		TestProcessComponentListener listener = new TestProcessComponentListener();

		IProcessComponent comp = TestUtil.executionFailComponent();
		comp.attachListener(listener);

		try {
			comp.execute();
		} catch (ProcessExecutionException ex) {
			// expected
		}

		assertFalse(listener.hasExecutionSucceeded());
		assertTrue(listener.hasExecutionFailed());
	}

	@Test
	public void testOnRollbackSuceeded() throws InvalidProcessStateException, ProcessRollbackException {

		TestProcessComponentListener listener = new TestProcessComponentListener();

		IProcessComponent comp = TestUtil.rollbackSuccessComponent();
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		comp.attachListener(listener);

		comp.rollback();

		assertTrue(listener.hasRollbackSucceeded());
		assertFalse(listener.hasRollbackFailed());
	}

	@Test
	public void testOnRollbackFailed() throws InvalidProcessStateException {

		TestProcessComponentListener listener = new TestProcessComponentListener();

		IProcessComponent comp = TestUtil.rollbackFailComponent();
		TestUtil.setState(comp, ProcessState.EXECUTION_FAILED);
		comp.attachListener(listener);

		try {
			comp.rollback();
		} catch (ProcessRollbackException ex) {
			// expected
		}

		assertFalse(listener.hasRollbackSucceeded());
		assertTrue(listener.hasRollbackFailed());
	}

}
