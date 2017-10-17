package cz.zajezdy.data.bengine.test.util;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineFactory;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.test.actions.LogAction;
import cz.zajezdy.data.bengine.test.configuration.model.ComputerDocument;
import cz.zajezdy.data.bengine.test.configuration.model.MultiTestDocument;
import cz.zajezdy.data.bengine.test.configuration.model.TestDocument;

import javax.script.ScriptException;
import java.util.Map;


public class TestHelper {

	public static RuleEngine buildEngineWithoutDoc(String configFile) {
		String json = ResourceFileHelper.getFileContentNoException(configFile);
		RuleEngine engine = RuleEngineFactory.getEngine(json);
		engine.registerAction("LogAction", new LogAction());
		return engine;
	}

	public static TypedRuleEngine<TestDocument> buildTestDocEngine(String configFile) {
		String json = ResourceFileHelper.getFileContentNoException(configFile);
		TypedRuleEngine<TestDocument> engine = RuleEngineFactory.getTypedEngine(json, TestDocument.class);
		engine.registerAction("LogAction", new LogAction());
		return engine;
	}

	public static <TDoc extends Document> TypedRuleEngine<TDoc> buildDocEngine(String configFile, Class<TDoc> docType) {
		String json = ResourceFileHelper.getFileContentNoException(configFile);
		TypedRuleEngine<TDoc> engine = RuleEngineFactory.getTypedEngine(json, docType);
		engine.registerAction("LogAction", new LogAction());
		return engine;
	}

	public static TypedRuleEngine<ComputerDocument> buildComputerDocEngine(String configFile) {
		String json = ResourceFileHelper.getFileContentNoException(configFile);
		TypedRuleEngine<ComputerDocument> engine = RuleEngineFactory.getTypedEngine(json, ComputerDocument.class);
		engine.registerAction("LogAction", new LogAction());
		return engine;
	}

	public static TypedRuleEngine<MultiTestDocument> buildMultiTestDocEngine(String configFile) {
		String json = ResourceFileHelper.getFileContentNoException(configFile);
		TypedRuleEngine<MultiTestDocument> engine = RuleEngineFactory.getTypedEngine(json, MultiTestDocument.class);
		engine.registerAction("LogAction", new LogAction());
		return engine;
	}

	public static MultiTestDocument getMultiTestDocument(Boolean expensive, Boolean test) throws ScriptException, InputValidationException {
		Map<String, Object> input = InputHelper.getMultioutputConfigurationInput(expensive, test);
		TypedRuleEngine<MultiTestDocument> re = TestHelper.buildMultiTestDocEngine("multioutput.json");
		re.executeRules(input);
		return re.getDocument();
	}
}
