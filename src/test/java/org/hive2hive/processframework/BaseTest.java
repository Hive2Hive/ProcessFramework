package org.hive2hive.processframework;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

	private static final String START_STRING = "Start ";
	private static final String END_STRING = "End ";

	protected static Class<? extends BaseTest> testClass;
	protected static Logger logger;

	@Rule
	public TestName name = new TestName();

	@Before
	public void beforeMethod() {
		logger.info(createMessage(name.getMethodName(), true));
	}

	@After
	public void afterMethod() {
		logger.info(createMessage(name.getMethodName(), false));
	}

	public static final void beforeClass() throws Exception {
		logger = LoggerFactory.getLogger(testClass);
		printTestIdentifier(testClass.getName(), true);
	}

	protected static void printTestIdentifier(String name, boolean isStart) {
		String bar = createBars(name, isStart);
		if (isStart) {
			logger.debug("");
		}
		logger.debug(bar);
		logger.info(createMessage(name, isStart));
		logger.debug(bar);
		if (!isStart) {
			logger.debug("");
		}

	}

	private static String createMessage(String name, boolean isStart) {
		StringBuffer message = new StringBuffer("--- ");
		if (isStart) {
			message.append(START_STRING);
		} else {
			message.append(END_STRING);
		}
		message.append(name);
		message.append(" ---");
		return message.toString();
	}

	private static String createBars(String name, boolean isStart) {
		StringBuffer bar = new StringBuffer();
		bar.append("----");
		int mitPart = name.length();
		if (isStart) {
			mitPart += START_STRING.length();
		} else {
			mitPart += END_STRING.length();
		}
		for (int i = 0; i < mitPart; i++) {
			bar.append("-");
		}
		bar.append("----");
		return bar.toString();
	}

	public static void afterClass() {
		printTestIdentifier(testClass.getName(), false);
	}

}
