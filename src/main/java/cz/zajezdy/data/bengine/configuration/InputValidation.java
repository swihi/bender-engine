package cz.zajezdy.data.bengine.configuration;

import java.util.List;


public interface InputValidation {

	public String getName();

	public String getType();

	public Boolean isMandatory();

	public List<Object> getAllowedValues();

	public String getExpression();
}
