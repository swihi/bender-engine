package cz.zajezdy.data.bengine;

import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.monitoring.PerformanceMarker;

/**
 * Interface for the JSRE RuleEngine.
 * 
 * @author Florian Beese
 */
public interface RuleEngine {

	/**
	 * Sets the configuration for the engine using an already Configuration
	 * object instead of JSON. This is normally created by deserializing the
	 * JSON configuration.
	 * 
	 * @param configuration The configuration of the rule engine.
	 */
	void setConfiguration(@SuppressWarnings("rawtypes") Configuration configuration);

	/**
	 * Sets the configuration.
	 * 
	 * @param jsonConfig A String containing the json configuration for the rule
	 *            engine.
	 */
	void setJsonConfiguration(String jsonConfig);

	/**
	 * Set's a JsonConverterProvider, which is used to deserialize the
	 * configuration. The JsonConverterProvider needs to provide two converters.
	 * One for deserializing the configuration and one for the document.
	 * 
	 * @param converterProdiver The converter provider for deserializing the
	 *            configuration and document.
	 */
	void setConverterProvider(JsonConverterProvider converterProdiver);

	/**
	 * Registers an Action with the given. The action can be executed
	 * by rules in the configuration in the "executionActions" element.
	 * 
	 * @param name The name of the Action.
	 * @param action The Action to be executed.
	 */
	void registerAction(String name, Action action);

	/**
	 * Set's the input for, which the rules will be executed on.
	 * 
	 * @param input A map of Objects (e.g. Integer, String ...) with the name of
	 *            the parameter in the configuration as key.
	 * @throws InputValidationException If a parameter does not match the the
	 *             validation rules, of the configuration an InputValidationException is
	 *             thrown containing information, what went wrong.
	 */
	void setInput(Map<String, Object> input) throws InputValidationException;

	/**
	 * Set's the input for, which the rules will be executed on.
	 * 
	 * @param input A map of Strings. The RuleEngine will try to convert the strings to the datatype specified in the configuration.
	 * @throws InputValidationException If a parameter does not match the the
	 *             validation rules, of the configuration an InputValidationException is
	 *             thrown containing information, what went wrong.
	 */
	void setInputStrings(Map<String, String> input) throws InputValidationException;

	/**
	 * Executes the rules defined in the configuration.
	 * Also executes post-processing actions.
	 * 
	 * @throws ScriptException During action evaluation a ScriptException may be
	 *             thrown, if the javascript code cannot be executed.
	 */
	void executeRules() throws ScriptException;

	/**
	 * Returns the document in the current state.
	 * Before rule execution this is the unchanged document from the
	 * configuration.
	 * After the rule execution it is the according to the rules modified
	 * document.
	 * 
	 * @return The document
	 */
	String getJsonDocument();

	/**
	 * Returns the document in a pretty printed way for better readability.
	 * 
	 * @see getJsonDocument
	 * @return The document
	 */
	String getJsonDocumentPrettyPrinted();

	/**
	 * Enables the performance monitoring of the rule engine.
	 * This slows down the whole execution a bit
	 */
	void enablePerformanceMonitoring();

	/**
	 * Enables the performance monitoring of the rule engine.
	 */
	void disablePerformanceMonitoring();

	/**
	 * 
	 * @return a list of performance markers for all operations
	 */
	List<PerformanceMarker> getPerformanceMonitoring();

	/**
	 * Prints a table to the stdout, containing information of
	 * the performance markers.
	 */
	void printPerformanceMonitoring();

	/**
	 * Returns the input of the engine as json. This may have been modified
	 * during runtime. If so the modified input is returned.
	 * 
	 * @return the input as json
	 */
	String getJsonInput();

	/**
	 * Returns the input of the engine as json. This may have been modified
	 * during runtime. If so the modified input is returned.
	 * The input is returned in a pretty printed way.
	 * 
	 * @return the input as json
	 */
	String getJsonInputPrettyPrinted();

	/**
	 * Enables the secure execution of the configuration java script.
	 * Calling this method disables access from the script to any java classes.
	 */
	void enableSecurity();

	/**
	 * Disables the secure execution of the configuration java script.
	 * Calling this method allows access from the script to any java classes.
	 */
	void disableSecurity();

}
