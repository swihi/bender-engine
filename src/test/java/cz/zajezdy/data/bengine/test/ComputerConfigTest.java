package cz.zajezdy.data.bengine.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineBuilder;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.test.configuration.model.ComputerConverterProvider;
import cz.zajezdy.data.bengine.test.configuration.model.ComputerDocument;
import cz.zajezdy.data.bengine.test.util.ExecutionHelper;
import cz.zajezdy.data.bengine.test.util.InputHelper;
import cz.zajezdy.data.bengine.test.util.ResourceFileHelper;


public class ComputerConfigTest {

	@Test
	public void testConverterProvider() throws IOException, ScriptException, InputValidationException {
		String json = ResourceFileHelper.getFileContent("cpuconfig.json");

		RuleEngineBuilder reb = new RuleEngineBuilder();
		reb.withJsonConverterProvider(new ComputerConverterProvider());
		reb.withJsonConfiguration(json);

		TypedRuleEngine<ComputerDocument> re = reb.buildTyped(ComputerDocument.class);
		assertNotNull(re);
		// execute(re, getRequest());
	}

	@Test
	public void typedTest() throws Exception {
		ExecutionHelper.execEngineWithComputerDocument("cpuconfig.json", InputHelper.getComputerConfigurationInput());
	}

	@Test
	public void testInputValidationTypes() throws ScriptException {
		Map<String, Object> input = InputHelper.getComputerConfigurationInput();
		input.put("ghzOption", new Integer(1));
		ExecutionHelper.execEngineWithComputerDocumentInputValidationError("cpuconfig.json", input, "ghzOption");
	}

}
