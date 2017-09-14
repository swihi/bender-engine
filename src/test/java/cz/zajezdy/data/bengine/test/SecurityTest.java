package cz.zajezdy.data.bengine.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.test.util.ExecutionHelper;
import cz.zajezdy.data.bengine.test.util.TestHelper;


public class SecurityTest {

	public static boolean logActionExecuted = false;

	public Map<String, Object> getInput() {
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("textInput", "test");
		return input;
	}

	public Map<String, Object> getExploitInput() {
		Map<String, Object> input = getInput();
		input.put("textInput", "exploit");
		return input;
	}

	@Test
	public void testConfig() throws Exception {
		ExecutionHelper.execEngineWithoutDoc("securitytest.json", getInput());
	}

	@Test
	public void testExploitWithoutSecurityEnabled() throws Exception {
		RuleEngine re = TestHelper.buildEngineWithoutDoc("securitytest.json");
		ExecutionHelper.execEngine(re, getExploitInput());
	}

	@Test
	public void testExploitWithSecurityEnabled() throws Exception {
		try {
			RuleEngine re = TestHelper.buildEngineWithoutDoc("securitytest.json");
			re.enableSecurity();
			String doc = ExecutionHelper.execEngine(re, getExploitInput());
			System.out.println(doc);
			fail();
		}
		catch (RuntimeException e) {
			assertTrue(e.getCause() instanceof ClassNotFoundException);
		}
	}
}
