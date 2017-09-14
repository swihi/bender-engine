package cz.zajezdy.data.bengine.test;

import org.junit.Before;
import org.junit.Test;
import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.cache.BasicRuleEngineProvider;
import cz.zajezdy.data.bengine.cache.RuleEngineCache;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.test.actions.LogAction;
import cz.zajezdy.data.bengine.test.util.ExecutionHelper;
import cz.zajezdy.data.bengine.test.util.InputHelper;
import cz.zajezdy.data.bengine.test.util.ResourceFileHelper;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CacheTest {

    private final boolean PRINT_INFO = false;

    private boolean threadHadError = false;

    @Before
    public void setUp() {
        RuleEngineCache.registerRuleEngineProvider(new BasicRuleEngineProvider() {

            @Override
            public String getConfigurationContent(String filename) throws IOException {
                return ResourceFileHelper.getFileContent(filename);
            }

            @Override
            protected void configureEngine(RuleEngine ruleEngine) {
                ruleEngine.registerAction("LogAction", new LogAction());
            }
        });
    }

    @Test
    public void testLocking() throws Exception {
        Map<String, Object> input = InputHelper.getTestDocumentInput();

        RuleEngine re = RuleEngineCache.get().getLockedRuleEngine("testconfig.json");
        ExecutionHelper.execEngine(re, input);
        RuleEngineCache.get().unlock(re);

        RuleEngine re2 = RuleEngineCache.get().getLockedRuleEngine("testconfig.json");
        ExecutionHelper.execEngine(re2, input);
        // re was unlocked, so it is available again and will be retrieved with
        // the next get()
        assertTrue(re.equals(re2));

        RuleEngine re3 = RuleEngineCache.get().getLockedRuleEngine("testconfig.json");
        ExecutionHelper.execEngine(re3, input);
        // re2 has not been unlocked, so for the next retrieval a new object has
        // to be created
        assertFalse(re2.equals(re3));
        RuleEngineCache.get().unlock(re2);
        RuleEngineCache.get().unlock(re3);

        if (PRINT_INFO) {
            System.out.println(RuleEngineCache.get().getStatistics());
        }
    }

    public class TestThread extends Thread {

        private int runCount = 0;

        public TestThread(int runCount) {
            this.runCount = runCount;
        }

        public void run() {
            Map<String, Object> input = InputHelper.getTestDocumentInput();
            for (int i = 0; i < runCount; i++) {
                RuleEngine re = null;
                try {
                    re = RuleEngineCache.get().getLockedRuleEngine("testconfig.json");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    ExecutionHelper.execEngine(re, input);
                } catch (InputValidationException | ScriptException e) {
                    threadHadError = true;
                    System.out.println("Encountered an error during execution: " + e.getMessage());
                }
                RuleEngineCache.get().unlock(re);
            }
            incrementFinished(this);
        }

    }

    private static int finished = 0;

    private synchronized static void incrementFinished(TestThread testThread) {
        // System.out.println(testThread.getName());
        finished++;
    }

    private double runThreadTest(int numThreads, int numRuns) {
        finished = 0;
        long startTime = System.nanoTime();
        // test with 8 concurrent threads
        for (int i = 0; i < numThreads; i++) {
            TestThread t = new TestThread(numRuns);
            t.start();
        }
        while (finished < numThreads) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        long endTime = System.nanoTime();
        double elapsedMicroSeconds = ((endTime - startTime) / 1000);
        return elapsedMicroSeconds;
    }

    @Test
    public void testConcurrencyWithoutCacheWarming() {
        threadHadError = false;
        double elapsed = runThreadTest(8, 8);
        assertFalse(threadHadError);
        if (PRINT_INFO) {
            System.out.println("Elapsed milliseconds: " + elapsed / 1000);
            System.out.println(RuleEngineCache.get().getStatistics());
        }
    }

    @Test
    public void testConcurrencyWithSingleCacheWarming() throws IOException {
        threadHadError = false;
        RuleEngine ruleEngine = RuleEngineCache.get().getLockedRuleEngine("testconfig.json");
        RuleEngineCache.get().unlock(ruleEngine);
        double elapsed = runThreadTest(8, 8);
        assertFalse(threadHadError);
        if (PRINT_INFO) {
            System.out.println("Elapsed milliseconds: " + elapsed / 1000);
            System.out.println(RuleEngineCache.get().getStatistics());
        }
    }

    @Test
    public void testConcurrencyWithWarming() {
        threadHadError = false;

        double total = 0;
        double elapsed = 0;
        int testRuns = 0;
        double min = Double.MAX_VALUE;
        double max = 0.0;

        runThreadTest(4, 6);

        for (testRuns = 0; testRuns < 6; testRuns++) {
            elapsed = runThreadTest(4, 6);
            total += elapsed;
            min = Math.min(elapsed, min);
            max = Math.max(elapsed, max);

            if (PRINT_INFO) {
                System.out.println("Elapsed milliseconds: " + elapsed / 1000);
                System.out.println(RuleEngineCache.get().getStatistics());
            }
        }
        assertFalse(threadHadError);

        max = max / 1000;
        min = min / 1000;
        total = total / 1000;
        if (PRINT_INFO) {
            System.out.println("Testruns: " + testRuns);
            System.out.println("Average: " + total / testRuns);
            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
            System.out.println("Total elapsed milliseconds: " + total);
        }
        //TODO: the comparison value should be set by the environment
        //and if not set the test should be ignored
        assertTrue(max < 120); // 12ms on core i7@3GHz
        assertTrue(total < 720); // 75ms on core i7@3GHz
    }

}
