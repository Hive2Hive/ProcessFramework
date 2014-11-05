package org.hive2hive.processframework.utils;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Random;

import org.hive2hive.processframework.ProcessComposite;
import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.composites.SyncProcess;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

public class TestUtil {

	private static Random r = new Random();

	/**
	 * Creates a {@link SyncProcess} that succeeds execution for testing purposes.
	 * 
	 * @return A {@link SyncProcess} that succeeds execution for testing purposes.
	 */
	public static SyncProcess executionSuccessSyncProcess() {

		SyncProcess pp = new SyncProcess();
		pp.add(executionSuccessComponent());
		pp.add(executionSuccessComponent());
		pp.add(executionSuccessComponent());

		return pp;
	}

	/**
	 * Creates a {@link SyncProcess} that fails execution for testing purposes.
	 * 
	 * @return A {@link SyncProcess} that fails execution for testing purposes.
	 */
	public static SyncProcess executionFailSyncProcess() {

		SyncProcess pp = new SyncProcess();
		pp.add(executionSuccessComponent());
		pp.add(executionSuccessComponent());
		pp.add(executionFailComponent());

		return pp;
	}

	/**
	 * Creates a {@link SyncProcess} that succeeds rollback for testing purposes.
	 * 
	 * @return A {@link SyncProcess} that succeeds rollback for testing purposes.
	 */
	public static SyncProcess rollbackSuccessSyncProcess() {

		SyncProcess pp = new SyncProcess();
		pp.add(rollbackSuccessComponent());
		pp.add(rollbackSuccessComponent());
		pp.add(rollbackSuccessComponent());

		return pp;
	}

	/**
	 * Creates a {@link SyncProcess} that fails rollback for testing purposes.
	 * 
	 * @return A {@link SyncProcess} that fails rollback for testing purposes.
	 */
	public static SyncProcess rollbackFailSyncProcess() {

		SyncProcess pp = new SyncProcess();
		// inverse
		pp.add(rollbackFailComponent());
		pp.add(rollbackSuccessComponent());
		pp.add(rollbackSuccessComponent());

		return pp;
	}

	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static ProcessComponent<Void> executionSuccessComponent() {

		return new ProcessComponent<Void>() {

			@Override
			protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				setRequiresRollback(true);
				return null;
			}

			@Override
			protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
				return null;
			}

			@Override
			public double getProgress() {
				return 0;
			}
		};
	}

	/**
	 * Creates an anonymous sample {@link ProcessComponent} that fails execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static ProcessComponent<Void> executionFailComponent() {

		return new ProcessComponent<Void>() {

			@Override
			protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				setRequiresRollback(true);
				throw new ProcessExecutionException(this, "Failing execution for testing purposes.");
			}

			@Override
			protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
				return null;
			}

			@Override
			public double getProgress() {
				return 0;
			}
		};
	}

	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds rollback for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static ProcessComponent<Void> rollbackSuccessComponent() {

		return new ProcessComponent<Void>() {

			@Override
			protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				setRequiresRollback(true);
				return null;
			}

			@Override
			protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
				return null;
			}

			@Override
			public double getProgress() {
				return 0;
			}
		};
	}

	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static ProcessComponent<Void> rollbackFailComponent() {

		return new ProcessComponent<Void>() {

			@Override
			protected Void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				setRequiresRollback(true);
				return null;
			}

			@Override
			protected Void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				throw new ProcessRollbackException(this, "Failing rollback for testing purposes.");
			}

			@Override
			public double getProgress() {
				return 0;
			}
		};
	}

	/**
	 * Creates a random {@link ProcessComposite} composite with the provided parameters for testing purposes.
	 * 
	 * @param maxDepth The maximal depth of the composite.
	 * @param maxBranchingFactor The maximal branching factor per level.
	 * @return A random {@link ProcessComposite} for testing purposes.
	 */
	public static ProcessComposite<Void> randomProcess(int maxDepth, int maxBranchingFactor) {

		ProcessComposite<Void> p = new SyncProcess();

		if (maxDepth > 0) {
			int d = r.nextInt(maxDepth + 1);
			int bf = r.nextInt(maxBranchingFactor + 1);
			while (bf-- > 0) {
				p.add(randomProcess(--d, maxBranchingFactor));
			}
		}
		return p;
	}

	/**
	 * Uses reflection to set the internal state of a process component.
	 * 
	 * @param object The object the state should be set on.
	 * @param state The state to be set.
	 */
	public static void setState(IProcessComponent<?> object, ProcessState state) {
		try {
			Method method = ProcessComponent.class.getDeclaredMethod("setState", ProcessState.class);
			method.setAccessible(true);
			method.invoke(object, state);
		} catch (Exception ex) {
			fail("Reflection failed.");
		}
	}
}
