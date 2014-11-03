package org.hive2hive.processframework.interfaces;

import org.hive2hive.processframework.ProcessComponent;

/**
 * Basic interface for process event arguments.
 * 
 * @author Christian Lüthold
 * 
 */
public interface IProcessEventArgs {

	/**
	 * Gets the original {@link ProcessComponent} that fired the event.
	 * 
	 * @return The original {@link ProcessComponent} that fired the event.
	 */
	IProcessComponent<?> getOriginalSource();
}
