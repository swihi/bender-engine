package cz.zajezdy.data.bengine.test;

import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineFactory;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.test.actions.LogAction;
import cz.zajezdy.data.bengine.test.util.InputHelper;
import cz.zajezdy.data.bengine.test.util.ResourceFileHelper;


public class PerformanceMonitoringTest {

	private static final String TEST_CONFIGURATION = "cpuconfig.json";

	@Test
	public void test() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		String json = ResourceFileHelper.getFileContentNoException("testconfig.json");
		RuleEngine re = RuleEngineFactory.getPerformanceMonitoredEngine(json);
		re.registerAction("LogAction", new LogAction());

		re.executeRules(input);
		re.printPerformanceMonitoring();

		// reset monitoring data:
		re.enablePerformanceMonitoring();

		re.executeRules(input);
		re.printPerformanceMonitoring();
	}

	@Test
	public void testOtherConfiguration() throws Exception {
		Map<String, Object> input = InputHelper.getComputerConfigurationInput();
		String json = ResourceFileHelper.getFileContentNoException(TEST_CONFIGURATION);
		RuleEngine re = RuleEngineFactory.getPerformanceMonitoredEngine(json);

		re.executeRules(input);
		re.printPerformanceMonitoring();

		// reset monitoring data:
		re.enablePerformanceMonitoring();

		re.executeRules(input);
		re.printPerformanceMonitoring();
	}

	@Test
	public void testMultipleRuns() throws Exception {
		Map<String, Object> input = InputHelper.getComputerConfigurationInput();
		String json = ResourceFileHelper.getFileContentNoException(TEST_CONFIGURATION);
		RuleEngine re = RuleEngineFactory.getEngine(json);
		measureMultipleRuns(re, input, 100);
	}

	@Test
	public void testMultipleRunsWithWarming() throws Exception {
		Map<String, Object> input = InputHelper.getComputerConfigurationInput();
		String json = ResourceFileHelper.getFileContentNoException(TEST_CONFIGURATION);
		RuleEngine re = RuleEngineFactory.getEngine(json);
		re.executeRules(input);
		measureMultipleRuns(re, input, 100);
	}

	private void measureMultipleRuns(RuleEngine re, Map<String, Object> input, int runCount) throws InputValidationException, ScriptException {
		long totalRuntime = 0;
		long minTime = 1000000000;
		int minRun = 0;
		long maxTime = 0;
		int maxRun = 0;
		long secondMaxTime = 0;
		int secondMaxRun = 0;
		long[] allTimes = new long[runCount];

		for (int i = 1; i <= runCount; i++) {
			long start = System.nanoTime();

			re.executeRules(input);

			long executionTime = System.nanoTime() - start;
			if (executionTime < minTime) {
				minTime = executionTime;
				minRun = i;
			}
			if (executionTime > maxTime) {
				maxTime = executionTime;
				maxRun = i;
			}
			if (executionTime > secondMaxTime && executionTime < maxTime) {
				secondMaxTime = executionTime;
				secondMaxRun = i;
			}
			totalRuntime += executionTime;
			allTimes[i - 1] = executionTime;
		}

		System.out.println("Execution statistics: ");
		System.out.println("Runs: " + runCount);
		System.out.println("Total time: " + nanoToMilli(totalRuntime) + " ms");
		System.out.println("Avg time: " + nanoToMilli(totalRuntime / runCount) + " ms");
		System.out.println("Min time: " + nanoToMilli(minTime) + " ms (in run: " + minRun + ")");
		System.out.println("Max time: " + nanoToMilli(maxTime) + " ms (in run: " + maxRun + ")");
		System.out.println("2nd Max time: " + nanoToMilli(secondMaxTime) + " ms (in run: " + secondMaxRun + ")");

		System.out.print("All times [");
		for (int i = 0; i < allTimes.length; i++) {
			if (i != 0) {
				System.out.print(", ");
			}
			System.out.print(Math.round(nanoToMilli(allTimes[i])));
		}
		System.out.println("]");

		// re.enablePerformanceMonitoring();
		// re.setInput(input);
		// re.executeRules();
		// re.printPerformanceMonitoring();
	}

	private double nanoToMilli(long nanoSeconds) {
		return (double) (nanoSeconds / 1000) / 1000;
	}

}
