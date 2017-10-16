package cz.zajezdy.data.bengine.configuration.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.InputValidation;
import cz.zajezdy.data.bengine.configuration.Rule;
import cz.zajezdy.data.bengine.configuration.converter.impl.JsonHelper;


public class BasicConfiguration implements Configuration {

	private String version;
	private Map<String, Object> document;
	private CopyOnWriteArrayList<BasicInputValidation> inputValidation;
	private CopyOnWriteArrayList<BasicRule> rules;
	private CopyOnWriteArrayList<String> postExecution;
	private CopyOnWriteArrayList<String> preExecution;
	private boolean rulesSorted = false;

	private String __jsonDocument;

	public String getVersion() {
		return version;
	}

	public List<? extends InputValidation> getInputValidations() {
		return inputValidation;
	}

	@Override
	public List<? extends Rule> getRules() {
		if (!rulesSorted) {
			sortRules(rules);
		}
		return rules;
	}

	private synchronized void sortRules(List<? extends Rule> rules) {
		// sort the rules synchronized on first access
		// ensures reusage in multithreading and correct behavior
		if (!rulesSorted) {
			rules.sort(Comparator.comparing(Rule::getPriority));
			rulesSorted = true;
		}
	}

	@Override
	public List<String> getPostExecution() {
		return postExecution;
	}

	@Override
	public List<String> getPreExecution() {
		return preExecution;
	}

	@Override
	public String getDocument() {
		if (__jsonDocument == null) {
			 __jsonDocument = JsonHelper.toJson(document);
		}
		return __jsonDocument;
	}

	public void setJsonDocument(String jsonDocument) {
		__jsonDocument = jsonDocument;
	}
}
