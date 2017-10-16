package cz.zajezdy.data.bengine.configuration;

import java.util.List;


public interface Configuration {

	String getVersion();

	List<? extends InputValidation> getInputValidations();

	List<? extends Rule> getRules();

	List<String> getPostExecution();

	List<String> getPreExecution();

	/**
	 * This is initial version of the output document.
	 *
	 * Output document is a data structure which will be returned from rule. Rule creators have this output document
	 * available as 'document' variable and they will modify it and after all rules run it will be returned back.
	 * @return JSON variation of the initial version of the output document
	 */
	String getDocument();

}
