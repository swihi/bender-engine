package cz.zajezdy.data.bengine.configuration.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.InputValidation;
import cz.zajezdy.data.bengine.configuration.Rule;


public class BasicConfiguration<TDoc extends Document> implements Configuration<TDoc> {

	private String version;
	private TDoc document;
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

	public TDoc getDocument() {
		return document;
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
	public String getJsonDocument() {
		return __jsonDocument;
	}

	@Override
	public void setJsonDocument(String jsonDocument) {
		__jsonDocument = jsonDocument;
	}
}
