package cz.zajezdy.data.bengine.test.util;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineFactory;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.test.actions.LogAction;
import cz.zajezdy.data.bengine.test.configuration.model.ComputerDocument;
import cz.zajezdy.data.bengine.test.configuration.model.TestDocument;


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
}
