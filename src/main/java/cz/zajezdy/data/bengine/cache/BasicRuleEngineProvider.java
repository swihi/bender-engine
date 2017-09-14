package cz.zajezdy.data.bengine.cache;

import java.io.IOException;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineBuilder;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.converter.impl.JsonDocumentConverterProvider;

public abstract class BasicRuleEngineProvider implements RuleEngineProvider {

	JsonConverterProvider converterProvider = new JsonDocumentConverterProvider();

	@SuppressWarnings("rawtypes")
	@Override
	public Configuration deserialize(String json) {
		JsonConverter<Configuration> converter = converterProvider.getConverter(Configuration.class);
		Configuration configuration = converter.fromJson(json);
		return configuration;
	}

	public abstract String getConfigurationContent(String filename) throws IOException;

	@Override
	public RuleEngine getEngine(@SuppressWarnings("rawtypes") Configuration configuration) {
		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withConfiguration(configuration);
		reb.withJsonConverterProvider(converterProvider);
		RuleEngine ruleEngine = reb.buildUntyped();
		configureEngine(ruleEngine);
		return ruleEngine;
	}

	protected abstract void configureEngine(RuleEngine ruleEngine);

}
