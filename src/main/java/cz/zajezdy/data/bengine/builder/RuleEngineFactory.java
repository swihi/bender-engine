package cz.zajezdy.data.bengine.builder;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.configuration.Document;


public class RuleEngineFactory {

	public static RuleEngine getSecureEngine(String jsonConfiguration) {
		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withJsonConfiguration(jsonConfiguration);
		reb.withSecurityEnabled();
		return reb.buildUntyped();
	}

	public static RuleEngine getEngine(String jsonConfiguration) {
		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withJsonConfiguration(jsonConfiguration);
		return reb.buildUntyped();
	}

	public static <TDoc extends Document> TypedRuleEngine<TDoc> getTypedEngine(String jsonConfiguration, Class<TDoc> docType) {
		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withJsonConfiguration(jsonConfiguration);
		return reb.buildTyped(docType);
	}

	public static RuleEngine getPerformanceMonitoredEngine(String jsonConfiguration) {
		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withJsonConfiguration(jsonConfiguration);
		reb.withPerformanceMonitoring();
		return reb.buildUntyped();
	}
}
