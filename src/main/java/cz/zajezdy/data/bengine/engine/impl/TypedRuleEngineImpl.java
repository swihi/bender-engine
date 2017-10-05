package cz.zajezdy.data.bengine.engine.impl;

import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.monitoring.PerformanceMarker;


/**
 * The TypedRuleEngine is a typed wrapper around the untyped DocumentRuleEngine.
 * 
 * @author Florian Beese
 *
 * @param <TDoc> The java type of the document. This class is created by
 * yourself.
 */

public class TypedRuleEngineImpl<TDoc extends Document> implements TypedRuleEngine<TDoc> {

	TypedRuleEngine<TDoc> ruleEngine = null;

	@SuppressWarnings("unchecked")
	protected TypedRuleEngineImpl() {
		ruleEngine = (TypedRuleEngine<TDoc>) new DocumentRuleEngine();
	}

	public void setJsonConfiguration(String jsonConfig) {
		ruleEngine.setJsonConfiguration(jsonConfig);
	}

	@Override
	public void setConfiguration(@SuppressWarnings("rawtypes") Configuration configuration) {
		ruleEngine.setConfiguration(configuration);
	}

	@Override
	public void setConverterProvider(JsonConverterProvider converterProdiver) {
		ruleEngine.setConverterProvider(converterProdiver);
	}

	@Override
	public void registerAction(String name, Action action) {
		ruleEngine.registerAction(name, action);
	}


	@Override
	public void executeRules(Map<String, Object> input) throws ScriptException, InputValidationException {
		ruleEngine.executeRules(input);
	}

	@Override
	public void executeRulesWithStringInput(Map<String, String> input) throws ScriptException, InputValidationException {
		ruleEngine.executeRulesWithStringInput(input);
	}

	@Override
	public String getJsonDocument() {
		return ruleEngine.getJsonDocument();
	}

	@Override
	public String getJsonDocumentPrettyPrinted() {
		return ruleEngine.getJsonDocumentPrettyPrinted();
	}

	@Override
	public TDoc getDocument() {
		return ruleEngine.getDocument();
	}

	@Override
	public void enablePerformanceMonitoring() {
		ruleEngine.enablePerformanceMonitoring();
	}

	@Override
	public void disablePerformanceMonitoring() {
		ruleEngine.disablePerformanceMonitoring();
	}

	@Override
	public List<PerformanceMarker> getPerformanceMonitoring() {
		return ruleEngine.getPerformanceMonitoring();
	}

	@Override
	public void printPerformanceMonitoring() {
		ruleEngine.printPerformanceMonitoring();
	}

	@Override
	public void enableSecurity() {
		ruleEngine.enableSecurity();
	}

	@Override
	public void disableSecurity() {
		ruleEngine.disableSecurity();
	}

}
