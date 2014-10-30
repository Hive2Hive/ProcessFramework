package org.hive2hive.processframework.interfaces;

import org.hive2hive.processframework.RollbackReason;

// TODO add documentation
public interface ICompletionHandle {

	void onCompletionSuccess();
	
	void onCompletionFailure(RollbackReason reason);
}
