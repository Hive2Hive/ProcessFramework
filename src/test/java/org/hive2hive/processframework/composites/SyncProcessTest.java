package org.hive2hive.processframework.composites;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hive2hive.processframework.BaseTest;
import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.ProcessStateTest;
import org.hive2hive.processframework.composites.SyncProcess;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponentListener;
import org.hive2hive.processframework.interfaces.IProcessEventArgs;
import org.hive2hive.processframework.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncProcessTest extends BaseTest {

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

		SyncProcess pp = new SyncProcess();
		assertTrue(pp.getState() == ProcessState.READY);
	}

	@Test
	public void testExecutionSuccess() {

		SyncProcess proc = TestUtil.executionSuccessSyncProcess();

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

		SyncProcess proc = TestUtil.executionFailSyncProcess();

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

		SyncProcess proc = TestUtil.rollbackSuccessSyncProcess();
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

		SyncProcess proc = TestUtil.rollbackFailSyncProcess();
		proc.execute();

		try {
			proc.rollback();
			fail("ProcessRollbackException should have been thrown.");
		} catch (ProcessRollbackException ex) {
			// expected
		}

		assertTrue(proc.getState() == ProcessState.ROLLBACK_FAILED);
	}

	@Test
	public void testExecutionOrder() throws InvalidProcessStateException, ProcessExecutionException {

		// define names
		String nameParent = "P";
		String nameChild1 = "C1";
		String nameChild2 = "C2";
		String nameChild3 = "C3";
		String nameChild4 = "C4";
		String nameChild5 = "C5";

		// define order of execution
		List<String> order = new ArrayList<String>();
		order.add(nameParent);
		order.add(nameChild1);
		order.add(nameChild2);
		order.add(nameChild3);
		order.add(nameChild4);
		order.add(nameChild5);

		SyncProcess p = new SyncProcess();
		ProcessComponent<?> c1 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c2 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c3 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c4 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c5 = TestUtil.executionSuccessComponent(true);
		p.add(c1);
		p.add(c2);
		p.add(c3);
		p.add(c4);
		p.add(c5);

		p.setName(nameParent);
		c1.setName(nameChild1);
		c2.setName(nameChild2);
		c3.setName(nameChild3);
		c4.setName(nameChild4);
		c5.setName(nameChild5);

		final Iterator<String> iterator = order.iterator();
		IProcessComponentListener listener = new IProcessComponentListener() {

			@Override
			public void onExecuting(IProcessEventArgs args) {
				String next = iterator.next();
				String sourceName = args.getSource().getName();

				assertTrue(next == sourceName);
			}

			@Override
			public void onRollbacking(IProcessEventArgs args) {
			}

			@Override
			public void onPaused(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionFailed(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackFailed(IProcessEventArgs args) {
			}
		};
		p.attachListener(listener);
		c1.attachListener(listener);
		c2.attachListener(listener);
		c3.attachListener(listener);
		c4.attachListener(listener);
		c5.attachListener(listener);

		p.execute();
	}

	@Test
	public void testRollbackOrder1() throws InvalidProcessStateException, ProcessExecutionException,
			ProcessRollbackException {

		// all components do require rollback
		
		// define names
		String nameParent = "P";
		String nameChild1 = "C1";
		String nameChild2 = "C2";
		String nameChild3 = "C3";
		String nameChild4 = "C4";
		String nameChild5 = "C5";

		// define order of rollback
		List<String> order = new ArrayList<String>();
		order.add(nameParent);
		order.add(nameChild5);
		order.add(nameChild4);
		order.add(nameChild3);
		order.add(nameChild2);
		order.add(nameChild1);

		SyncProcess p = new SyncProcess();
		ProcessComponent<?> c1 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c2 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c3 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c4 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c5 = TestUtil.executionSuccessComponent(true);
		p.add(c1);
		p.add(c2);
		p.add(c3);
		p.add(c4);
		p.add(c5);

		p.setName(nameParent);
		c1.setName(nameChild1);
		c2.setName(nameChild2);
		c3.setName(nameChild3);
		c4.setName(nameChild4);
		c5.setName(nameChild5);

		final Iterator<String> iterator = order.iterator();
		IProcessComponentListener listener = new IProcessComponentListener() {

			@Override
			public void onExecuting(IProcessEventArgs args) {
			}

			@Override
			public void onRollbacking(IProcessEventArgs args) {
				String next = iterator.next();
				String sourceName = args.getSource().getName();
				
				assertTrue(next == sourceName);
			}

			@Override
			public void onPaused(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionFailed(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackFailed(IProcessEventArgs args) {
			}
		};
		p.attachListener(listener);
		c1.attachListener(listener);
		c2.attachListener(listener);
		c3.attachListener(listener);
		c4.attachListener(listener);
		c5.attachListener(listener);

		p.execute();
		p.rollback();
	}
	
	@Test
	public void testRollbackOrder2() throws InvalidProcessStateException, ProcessExecutionException,
			ProcessRollbackException {

		// some components do not require rollback (C3, C5)
		
		// define names
		String nameParent = "P";
		String nameChild1 = "C1";
		String nameChild2 = "C2";
		String nameChild3 = "C3";
		String nameChild4 = "C4";
		String nameChild5 = "C5";

		// define order of rollback
		List<String> order = new ArrayList<String>();
		order.add(nameParent);
		order.add(nameChild4);
		order.add(nameChild2);
		order.add(nameChild1);

		SyncProcess p = new SyncProcess();
		ProcessComponent<?> c1 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c2 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c3 = TestUtil.executionSuccessComponent(false);
		ProcessComponent<?> c4 = TestUtil.executionSuccessComponent(true);
		ProcessComponent<?> c5 = TestUtil.executionSuccessComponent(false);
		p.add(c1);
		p.add(c2);
		p.add(c3);
		p.add(c4);
		p.add(c5);

		p.setName(nameParent);
		c1.setName(nameChild1);
		c2.setName(nameChild2);
		c3.setName(nameChild3);
		c4.setName(nameChild4);
		c5.setName(nameChild5);

		final Iterator<String> iterator = order.iterator();
		IProcessComponentListener listener = new IProcessComponentListener() {

			@Override
			public void onExecuting(IProcessEventArgs args) {
			}

			@Override
			public void onRollbacking(IProcessEventArgs args) {
				String next = iterator.next();
				String sourceName = args.getSource().getName();
				
				assertTrue(next == sourceName);
			}

			@Override
			public void onPaused(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onExecutionFailed(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackSucceeded(IProcessEventArgs args) {
			}

			@Override
			public void onRollbackFailed(IProcessEventArgs args) {
			}
		};
		p.attachListener(listener);
		c1.attachListener(listener);
		c2.attachListener(listener);
		c3.attachListener(listener);
		c4.attachListener(listener);
		c5.attachListener(listener);

		p.execute();
		p.rollback();
	}
}
