package org.hive2hive.processframework.steps;

import org.hive2hive.processframework.ProcessStep;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;

/**
 * A process step that intends to return a result.
 * 
 * @author Christian Lüthold
 * 
 * @param <T> The type of the result object.
 */
public abstract class ResultProcessStep<T> extends ProcessStep implements IResultProcessComponent<T> {



}
