package org.hive2hive.processframework.decorators;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.hive2hive.processframework.BaseTest;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.processframework.utils.BusyComponent;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncComponentTest extends BaseTest {

	@BeforeClass
	public static void initTest() throws Exception {
		testClass = AsyncComponentTest.class;
		beforeClass();
	}

	@AfterClass
	public static void endTest() {
		afterClass();
	}

	@Test
	public void testInstantiation() {

		IProcessComponent<Void> decoratedComponent = TestUtil.executionSuccessComponent(true);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(decoratedComponent);

		assertTrue(ac.getState() == ProcessState.READY);
		assertTrue(ac.getState() == decoratedComponent.getState());
	}

	@Test
	public void testExecutionSuccess() throws ExecutionException {

		IProcessComponent<Void> decoratedComponent = TestUtil.executionSuccessComponent(true);
		IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

		Future<?> future = null;
		try {
			future = ac.execute();
		} catch (InvalidProcessStateException | ProcessExecutionException ex) {
			fail(ex.getMessage());
		}

		try {
			future.get();
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof ProcessExecutionException) {
				fail("Should execute successfully.");
			} else {
				throw ex;
			}
		}

		assertTrue(ac.getState() == ProcessState.EXECUTION_SUCCEEDED);
		assertTrue(ac.getState() == decoratedComponent.getState());
	}

	@Test(expected = ProcessExecutionException.class)
	public void testExecutionFail() throws ProcessExecutionException, ExecutionException {

		IProcessComponent<Void> decoratedComponent = TestUtil.executionFailComponent(true);
		IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

		Future<?> future = null;
		try {
			future = ac.execute();
		} catch (InvalidProcessStateException | ProcessExecutionException ex1) {
			fail();
		}

		try {
			future.get();
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof ProcessExecutionException) {
				// expected, throw
				throw (ProcessExecutionException) ex.getCause();
			} else {
				throw ex;
			}
		} finally {
			assertTrue(ac.getState() == ProcessState.EXECUTION_FAILED);
			assertTrue(ac.getState() == decoratedComponent.getState());
		}
	}

	@Test
	public void testRollbackSuccess1() throws InvalidProcessStateException, ProcessExecutionException,
			InterruptedException, ExecutionException {

		// test executed component (execution completed)
		IProcessComponent<Void> decoratedComponent = TestUtil.rollbackSuccessComponent();
		IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

		ac.execute().get(); // wait for completion

		Future<?> future = null;
		try {
			future = ac.rollback();
		} catch (InvalidProcessStateException | ProcessRollbackException ex) {
			fail(ex.getMessage());
		}

		try {
			future.get();
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof ProcessRollbackException) {
				fail("Should execute successfully.");
			} else {
				throw ex;
			}
		}

		assertTrue(ac.getState() == ProcessState.ROLLBACK_SUCCEEDED);
		assertTrue(ac.getState() == decoratedComponent.getState());
	}

	@Test(expected = ProcessRollbackException.class)
	public void testRollbackFail1() throws InvalidProcessStateException, ProcessExecutionException,
			InterruptedException, ExecutionException, ProcessRollbackException {

		// test executed component (execution completed)
		IProcessComponent<Void> decoratedComponent = TestUtil.rollbackFailComponent();
		IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

		ac.execute().get(); // wait for completion

		Future<?> future = null;
		try {
			future = ac.rollback();
		} catch (InvalidProcessStateException | ProcessRollbackException ex) {
			fail(ex.getMessage());
		}

		try {
			future.get();
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof ProcessRollbackException) {
				// expected, throw
				throw (ProcessRollbackException) ex.getCause();
			} else {
				throw ex;
			}
		} finally {
			assertTrue(ac.getState() == ProcessState.ROLLBACK_FAILED);
			assertTrue(ac.getState() == decoratedComponent.getState());
		}
	}

	@Test
	public void testRollbackSuccess2() throws InvalidProcessStateException, ProcessExecutionException,
			InterruptedException, ExecutionException {

		// test executing component (execution ongoing)
		IProcessComponent<Void> decoratedComponent = TestUtil.rollbackSuccessComponent();
		IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
		AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

		ac.execute(); // don't wait for completion

		Future<?> future = null;
		try {
			future = ac.rollback();
		} catch (InvalidProcessStateException | ProcessRollbackException ex) {
			fail(ex.getMessage());
		}

		try {
			future.get();
		} catch (InterruptedException ex) {
			fail(ex.getMessage());
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof ProcessRollbackException) {
				fail("Should execute successfully.");
			} else {
				throw ex;
			}
		}

		assertTrue(ac.getState() == ProcessState.ROLLBACK_SUCCEEDED);
		assertTrue(ac.getState() == decoratedComponent.getState());
	}

	@Test
	public void testRollbackFail2() throws InvalidProcessStateException, ProcessExecutionException,
			InterruptedException, ExecutionException, ProcessRollbackException {

		for (int i = 0; i < 100; i++) {
			logger.debug(String.format("Run %s/100.", i + 1));

			// test executing component (execution ongoing)
			IProcessComponent<Void> decoratedComponent = TestUtil.rollbackFailComponent();
			IProcessComponent<Void> busyComponent = new BusyComponent(decoratedComponent);
			AsyncComponent<Void> ac = new AsyncComponent<Void>(busyComponent);

			ac.execute(); // don't wait for completion

			Future<?> future = null;
			try {
				future = ac.rollback();
			} catch (InvalidProcessStateException | ProcessRollbackException ex) {
				fail(ex.getMessage());
			}

			try {
				future.get();
			} catch (InterruptedException ex) {
				fail(ex.getMessage());
			} catch (ExecutionException ex) {
				assertTrue(ex.getCause() instanceof ProcessRollbackException);
			} finally {
				assertTrue(ac.getState() == ProcessState.ROLLBACK_FAILED);
				assertTrue(ac.getState() == decoratedComponent.getState());
			}
		}
	}
}