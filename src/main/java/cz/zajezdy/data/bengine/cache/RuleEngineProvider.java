package cz.zajezdy.data.bengine.cache;

import java.io.IOException;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.configuration.Configuration;

public interface RuleEngineProvider {

	public String getConfigurationContent(String filename) throws IOException;

	@SuppressWarnings("rawtypes")
	public Configuration deserialize(String json);

	@SuppressWarnings("rawtypes")
	public RuleEngine getEngine(Configuration configuration);
}
