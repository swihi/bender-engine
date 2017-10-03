package cz.zajezdy.data.bengine.configuration;

import java.util.List;


public interface Configuration<TDoc extends Document> {

	String getVersion();

	List<? extends InputValidation> getInputValidations();

	TDoc getDocument();

	List<? extends Rule> getRules();

	List<String> getPostExecution();

	List<String> getPreExecution();

	/**
	 * Get JSON version of TDoc getDocument
	 */
	String getJsonDocument();

	/**
	 * Set JSON version of TDoc document
	 * It is necessary to set jsonDocument just after new Configuration is created, because it is used when creating
	 * script for eval
	 */
	void setJsonDocument(String jsonDoc);
}
