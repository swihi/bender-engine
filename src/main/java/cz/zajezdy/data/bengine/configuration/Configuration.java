package cz.zajezdy.data.bengine.configuration;

import java.util.List;


public interface Configuration<TDoc extends Document> {

	public String getVersion();

	public List<? extends InputValidation> getInputValidations();

	public TDoc getDocument();

	public List<? extends Rule> getRules();

	public List<String> getPostExecution();

	public List<String> getPreExecution();

	public String getJsonDocument();

	public void setJsonDocument(String jsonDoc);
}
