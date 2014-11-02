package org.hive2hive.processframework.utils;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.hive2hive.processframework.ProcessComponent;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.RollbackReason;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.exceptions.ProcessRollbackException;
import org.hive2hive.processframework.interfaces.IProcessComponent;

public class TestUtil {

	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static IProcessComponent executionSuccessComponent() {

		return new ProcessComponent() {

			@Override
			protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				// do nothing
			}

			@Override
			protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
			}

			@Override
			protected void doPause() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeExecution() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeRollback() throws InvalidProcessStateException {
				// do nothing
			}
		};
	}
	
	/**
	 * Creates an anonymous sample {@link ProcessComponent} that fails execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static IProcessComponent executionFailComponent() {

		return new ProcessComponent() {

			@Override
			protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				throw new ProcessExecutionException("Failing execution for testing purposes.");
			}

			@Override
			protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
			}

			@Override
			protected void doPause() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeExecution() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeRollback() throws InvalidProcessStateException {
				// do nothing
			}
		};
	}
	
	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds rollback for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static IProcessComponent rollbackSuccessComponent() {

		return new ProcessComponent() {

			@Override
			protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				// do nothing
			}

			@Override
			protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				// do nothing
			}

			@Override
			protected void doPause() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeExecution() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeRollback() throws InvalidProcessStateException {
				// do nothing
			}
		};
	}
	
	/**
	 * Creates an anonymous sample {@link ProcessComponent} that succeeds execution for testing purposes.
	 * 
	 * @return An anonymous {@link ProcessComponent} for testing purposes.
	 */
	public static IProcessComponent rollbackFailComponent() {

		return new ProcessComponent() {

			@Override
			protected void doExecute() throws InvalidProcessStateException, ProcessExecutionException {
				// do nothing
			}

			@Override
			protected void doRollback() throws InvalidProcessStateException, ProcessRollbackException {
				throw new ProcessRollbackException("Failing rollback for testing purposes.");
			}

			@Override
			protected void doPause() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeExecution() throws InvalidProcessStateException {
				// do nothing
			}

			@Override
			protected void doResumeRollback() throws InvalidProcessStateException {
				// do nothing
			}
		};
	}
	
	/**
	 * Creates a sample {@link RollbackReason} for testing purposes.
	 * 
	 * @return A sample {@link RollbackReason} for testing purposes.
	 */
	public static RollbackReason sampleRollbackReason() {

		return new RollbackReason("This is a sample rollback reason for testing purposes.");
	}
	
	/**
	 * Uses reflection to set the internal state of a process component.
	 * 
	 * @param object The object the state should be set on.
	 * @param state The state to be set.
	 */
	public static void setState(IProcessComponent object, ProcessState state) {
		try {
			Method method = ProcessComponent.class.getDeclaredMethod("setState", ProcessState.class);
			method.setAccessible(true);
			method.invoke(object, state);
		} catch (Exception ex) {
			fail("Reflection failed.");
		}
	}
}
