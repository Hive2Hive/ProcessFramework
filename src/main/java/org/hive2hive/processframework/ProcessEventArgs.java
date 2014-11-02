package org.hive2hive.processframework;

import org.hive2hive.processframework.interfaces.IProcessEventArgs;

/**
 * Default process event arguments.
 * 
 * @author Christian Lüthold
 *
 */
public class ProcessEventArgs implements IProcessEventArgs {

	private ProcessComponent originalSource;

	public ProcessEventArgs(ProcessComponent originalSource) {
		this.originalSource = originalSource;
	}

	@Override
	public ProcessComponent getOriginalSource() {
		return originalSource;
	}

}
