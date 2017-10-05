package cz.zajezdy.data.bengine.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.script.ScriptException;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.test.configuration.model.ComputerDocument;
import cz.zajezdy.data.bengine.test.configuration.model.TestDocument;


public class ExecutionHelper {

	public static String execEngine(RuleEngine re, Map<String, Object> input) throws InputValidationException, ScriptException {
		re.executeRules(input);

		return re.getJsonDocumentPrettyPrinted();
	}

	public static String execEngineWithTestDocument(String configFile, Map<String, Object> input) throws ScriptException, InputValidationException {
		TypedRuleEngine<TestDocument> re = TestHelper.buildTestDocEngine(configFile);
		return execEngineWithTestDocument(re, input);
	}

	public static String execEngineWithTestDocumentStringInput(TypedRuleEngine<TestDocument> re, Map<String, String> input)
			throws ScriptException, InputValidationException {
		TestDocument d1 = re.getDocument();
		assertFalse(d1.getTest());

		re.executeRulesWithStringInput(input);

		TestDocument d2 = re.getDocument();
		assertTrue(d2.getTest());

		return re.getJsonDocumentPrettyPrinted();
	}

	public static String execEngineWithTestDocument(TypedRuleEngine<TestDocument> re, Map<String, Object> input)
			throws ScriptException, InputValidationException {
		TestDocument d1 = re.getDocument();
		assertFalse(d1.getTest());

		re.executeRules(input);

		TestDocument d2 = re.getDocument();
		assertTrue(d2.getTest());

		return re.getJsonDocumentPrettyPrinted();
	}

	public static void execEngineWithTestDocumentInputValidationError(String configFile, Map<String, Object> input, String inputParamName)
			throws ScriptException {
		try {
			execEngineWithTestDocument(configFile, input);
			fail();
		}
		catch (InputValidationException e) {
			assertTrue(e.getMessage().contains(inputParamName));
		}
	}

	public static String execEngineWithComputerDocument(String configFile, Map<String, Object> request) throws ScriptException, InputValidationException {
		TypedRuleEngine<ComputerDocument> re = TestHelper.buildDocEngine(configFile, ComputerDocument.class);

		ComputerDocument d1 = re.getDocument();
		assertEquals(10.0, d1.getGhz().get("1.5").getPrice(), 0.0001);

		re.executeRules(request);

		ComputerDocument d2 = re.getDocument();
		assertEquals(20.0, d2.getGhz().get("1.5").getPrice(), 0.0001);

		String pureJsonDoc = re.getJsonDocumentPrettyPrinted();
		// System.out.println("JSON Document: " + pureJsonDoc);
		return pureJsonDoc;
	}

	public static void execEngineWithComputerDocumentInputValidationError(String configFile, Map<String, Object> input, String inputParamName)
			throws ScriptException {
		try {
			execEngineWithComputerDocument(configFile, input);
			fail();
		}
		catch (InputValidationException e) {
			assertTrue(e.getMessage().contains(inputParamName));
		}
	}

	public static String execEngineWithoutDoc(String configFile, Map<String, Object> input) throws InputValidationException, ScriptException {
		RuleEngine re = TestHelper.buildEngineWithoutDoc(configFile);
		return execEngine(re, input);
	}
}
