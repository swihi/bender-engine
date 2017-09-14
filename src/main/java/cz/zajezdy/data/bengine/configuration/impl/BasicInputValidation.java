package cz.zajezdy.data.bengine.configuration.impl;

import java.util.List;

import cz.zajezdy.data.bengine.configuration.InputValidation;


public class BasicInputValidation implements InputValidation {

	private String name;
	private String type;
	private Boolean mandatory;
	private List<Object> allowedValues;
	private String expression;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Boolean isMandatory() {
		return mandatory;
	}

	@Override
	public List<Object> getAllowedValues() {
		return allowedValues;
	}

	@Override
	public String getExpression() {
		return expression;
	}
}
