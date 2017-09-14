package cz.zajezdy.data.bengine.configuration.impl;

import java.util.List;

import cz.zajezdy.data.bengine.configuration.Rule;

public class BasicRule implements Rule {

	private String expression;
	private String description;
	private Integer priority;
	private List<String> scriptActions;
	private List<String> executionActions;
	
	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<String> getScriptActions() {
		return scriptActions;
	}

	@Override
	public List<String> getExecutionActions() {
		return executionActions;
	}

	@Override
	public Integer getPriority() {
		return priority;
	}

}
