package cz.zajezdy.data.bengine.engine.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.google.common.base.Strings;
import cz.zajezdy.data.bengine.engine.ScriptBuilderType;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.action.Action;
import cz.zajezdy.data.bengine.action.ParameterizedAction;
import cz.zajezdy.data.bengine.action.SimpleAction;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.InputValidation;
import cz.zajezdy.data.bengine.configuration.Rule;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.engine.internal.scriptengine.JavaScriptEngine;
import cz.zajezdy.data.bengine.engine.internal.scriptengine.JavaScriptEngineFactory;
import cz.zajezdy.data.bengine.engine.internal.scriptengine.impl.JavaScriptEngineFactoryImpl;
import cz.zajezdy.data.bengine.exception.InputValidationException;
import cz.zajezdy.data.bengine.exception.InvalidConfigurationException;
import cz.zajezdy.data.bengine.monitoring.PerformanceMarker;
import cz.zajezdy.data.bengine.monitoring.PerformanceMarkerMgr;
import cz.zajezdy.data.bengine.monitoring.PerformanceMarkerPrinter;


public abstract class AbstractRuleEngine implements RuleEngine {

	@SuppressWarnings("rawtypes")
	private Configuration configuration;
	private JsonConverterProvider converterProdiver;
	private JavaScriptEngine engine = null;
	private HashMap<String, Action> registeredActions = new HashMap<String, Action>();
	protected boolean performanceMonitoring = false;
	private boolean enableSecurity = false;

	private CompiledScript compiledScript = null;

	// @SuppressWarnings("unused")
	// private Class<? extends Document> documentType;

	protected String jsonDoc = null;

	private PerformanceMarkerMgr performanceMarkerMgr;

	public AbstractRuleEngine() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jsre.RuleEngineType#setConfiguration(com.jsre.configuration.
	 * Configuration)
	 */
	@Override
	public void setConfiguration(@SuppressWarnings("rawtypes") Configuration configuration) {
		this.configuration = configuration;
		resetDocument();
	}

	// public void setDocumentType(Class<? extends Document> documentType) {
	// this.documentType = documentType;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jsre.RuleEngineType#setJsonConfiguration(java.lang.String)
	 */
	@Override
	public void setJsonConfiguration(String jsonConfig) {
		setPerformanceMarker("setJsonConfiguration call");
		this.configuration = getConfiguration(jsonConfig, this.converterProdiver);
		resetDocument();
		setPerformanceMarker("setJsonConfiguration finished");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jsre.RuleEngineType#setConverterProvider(com.jsre.configuration.
	 * converter.JsonConverterProvider)
	 */
	@Override
	public void setConverterProvider(JsonConverterProvider converterProdiver) {
		this.converterProdiver = converterProdiver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jsre.RuleEngineType#registerAction(java.lang.String,
	 * com.jsre.action.Action)
	 */
	@Override
	public void registerAction(String name, Action action) {
		registeredActions.put(name, action);
	}

	// tries to get the correct engine for the java version 7 or 8
	// and apply security to it
	private JavaScriptEngine getEngine() {
		JavaScriptEngineFactory factory = new JavaScriptEngineFactoryImpl();
		factory.enableStandardSecurity(enableSecurity);
		if (performanceMonitoring) {
			factory.registerPerformanceMarkerMgr(performanceMarkerMgr);
		}
		return factory.getEngine();
	}

	private void initEngine() {
		if (engine == null) {
			setPerformanceMarker("starting engine initialization");
			engine = getEngine();
			setPerformanceMarker("initializating engine");
			try {
				engine.eval("var value=null;");
			}
			catch (ScriptException e) {
				// won't happen
				e.printStackTrace();
			}
			setPerformanceMarker("finished engine initialization");
		}
	}

	private void resetEngine() {
		setPerformanceMarker("call resetEngine");
		initEngine();

		resetDocument();
		setPerformanceMarker("finished resetEngine");
	}

	@SuppressWarnings("rawtypes")
	private Configuration getConfiguration(String json, JsonConverterProvider converterProdiver) {
		JsonConverter<Configuration> converter = converterProdiver.getConverter(Configuration.class);
		Configuration config = converter.fromJson(json);
		return config;
	}

	private Integer doubleToInteger(Object oDouble) {
		return new Integer(((Double) oDouble).intValue());
	}

	@SuppressWarnings("unchecked")
	private <T> void checkValueAllowed(String name, List<Object> allowedValues, Object value, Class<T> type) throws InputValidationException {
		if (allowedValues == null) {
			return;
		}
		T val = (T) value;
		boolean checkOkay = false;
		for (Object aV : allowedValues) {
			T allowedValue = (T) aV;
			// TODO: workaround for lack of GSON to determine Integer correctly
			// see:
			// http://stackoverflow.com/questions/17090589/gson-deserialize-integers-as-integers-and-not-as-doubles
			if (type == Integer.class && aV instanceof Double) {
				allowedValue = (T) doubleToInteger(aV);
			}
			if (allowedValue.equals(val)) {
				checkOkay = true;
				break;
			}
		}
		if (!checkOkay) {
			String sAllowedValues = null;
			if (type == Integer.class) {
				sAllowedValues = "[";
				boolean first = true;
				for (Object aV : allowedValues) {
					if (!first) {
						sAllowedValues += ", ";
					}
					T allowedValue = (T) doubleToInteger(aV);
					sAllowedValues += allowedValue.toString();
					if (first) {
						first = false;
					}
				}
				sAllowedValues += "]";
			}
			else {
				sAllowedValues = allowedValues.toString();
			}
			throw new InputValidationException("The value '" + value + "' of parameter '" + name + "' is not allowed. Allowed values are: " + sAllowedValues);
		}
	}

	private String escapeStringValue(String value) {
		return value.replace("\'", "\\'");
	}

	private <T> void checkExpression(String name, String expression, Object value, Class<T> type) throws InputValidationException {
		if (StringUtils.isEmpty(expression)) {
			return;
		}
		String varSet = "";
		if (type == String.class) {
			varSet = "value='" + escapeStringValue((String) value) + "';";
		}
		else {
			varSet = "value=" + value + ";";
		}
		Boolean valid = null;
		try {
			engine.eval(varSet);
		}
		catch (ScriptException e) {
			throw new InputValidationException("Was not able to interpret input parameter '" + name + "' in a JS context.");
		}
		try {
			valid = (Boolean) engine.eval(expression);
		}
		catch (ScriptException e) {
			throw new InvalidConfigurationException("The expression '" + expression + "' for input parameter '" + name + "' is not valid javascript!");
		}
		catch (ClassCastException e) {
			throw new InvalidConfigurationException("The expression '" + expression + "' for input parameter '" + name + "' does not evaluate to Boolean!");
		}
		if (!valid) {
			throw new InputValidationException("Input parameter '" + name + "' did not validate against the expression '" + expression + "'");
		}
	}

	private <T> void checkForType(InputValidation iv, String name, Object data, Class<T> type) throws InputValidationException {
		if (!type.isInstance(data)) {
			throw new InputValidationException("The value of '" + name + "' was not of type '" + type.getSimpleName() + "'.");
		}
		checkValueAllowed(name, iv.getAllowedValues(), data, type);
		checkExpression(name, iv.getExpression(), data, type);
	}

	@SuppressWarnings("unchecked")
	private void checkInput(InputValidation iv, String name, Object data) throws InputValidationException {
		String type = iv.getType();

		switch (type) {
		// case "Code":
		// break;
		case "Integer":
			checkForType(iv, name, data, Integer.class);
			break;
		case "Double":
			checkForType(iv, name, data, Double.class);
			break;
		case "String":
			checkForType(iv, name, data, String.class);
			break;
		case "Boolean":
			checkForType(iv, name, data, Boolean.class);
			break;
		default:
			if (type.contains(".")) {
				@SuppressWarnings("rawtypes")
				Class t;
				try {
					t = Class.forName(iv.getType());
				}
				catch (ClassNotFoundException e) {
					//@formatter:off
					throw new InvalidConfigurationException(
							"the type '" + type + "' found at input param definition '" + name + 
							"could not be found via reflection.");
					//@formatter:on
				}
				checkForType(iv, name, data, t);
			}
			else {
				//@formatter:off
				throw new InvalidConfigurationException(
					"the type '" + type + "' found at input param definition '" + name + 
					"' is not supported. Supported types are Integer, Double, String, Boolean " +
					"and full qualified class names of classes having an <init>(String) contructor.");
				//@formatter:on
			}
		}
	}

	public CompiledScript getCompiledScript() throws ScriptException {
		String script;
		if (ScriptBuilderType.MULTIOUTPUT.equals(configuration.getScriptBuilderType())) {
			script = MultioutputScriptBuilder.getScript(configuration, registeredActions);
		} else {
			script = BasicScriptBuilder.getScript(configuration, registeredActions);
		}

		if (compiledScript == null) {
			compiledScript = engine.getCompiledScript(script);
		}
		return compiledScript;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.zajezdy.data.bengine.RuleEngineType#executeRules()
	 */
	@Override
	public void executeRules(Map<String, Object> input) throws ScriptException, InputValidationException {
		setPerformanceMarker("executeRules call");

		resetEngine();

		validateInputMap(input);

		// compiles the script first time
		getCompiledScript();

		String jsonInput = new Gson().toJson(input);

		try {
			Object object = engine.invokeFunction("executeScript", jsonInput, registeredActions);
			jsonDoc = (String) object;
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		setPerformanceMarker("executeRules finished");
	}

	@Override
	public void executeRulesWithStringInput(Map<String, String> input) throws ScriptException, InputValidationException {
		executeRules(validateAndConvertInputMap(input));
	}

	@Override
	public String getJsonDocument() {
		return jsonDoc;
	}

	private String prettifyJson(String json) {
		try {
			return (String) engine.eval("var __makeMePretty = " + json + ";JSON.stringify(__makeMePretty, null, 2);");
		}
		catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getJsonDocumentPrettyPrinted() {
		return prettifyJson(jsonDoc);
	}

	protected void eval(String javascript) throws ScriptException {
		engine.eval(javascript);
	}

	@SuppressWarnings("rawtypes")
	protected Configuration getConfiguration() {
		return configuration;
	}

	protected JsonConverterProvider getConverterProvider() {
		return converterProdiver;
	}

	@Override
	public void enablePerformanceMonitoring() {
		this.performanceMonitoring = true;
		this.performanceMarkerMgr = new PerformanceMarkerMgr();
	}

	@Override
	public void enableSecurity() {
		this.enableSecurity = true;
	}

	@Override
	public void disableSecurity() {
		this.enableSecurity = false;
	}

	@Override
	public void disablePerformanceMonitoring() {
		this.performanceMonitoring = false;
	}

	@Override
	public List<PerformanceMarker> getPerformanceMonitoring() {
		if (performanceMarkerMgr == null) {
			return null;
		}
		return performanceMarkerMgr.getPerformanceMarkers();
	}

	@Override
	public void printPerformanceMonitoring() {
		PerformanceMarkerPrinter printer = new PerformanceMarkerPrinter();
		printer.printList(getPerformanceMonitoring());
	}

	// compiler should inline this method
	private final void setPerformanceMarker(final String marker) {
		if (performanceMonitoring) {
			performanceMarkerMgr.addMarker(marker);
		}
	}

	private void validateInputMap(Map<String, Object> input) throws InputValidationException {
		setPerformanceMarker("process input");
		@SuppressWarnings("unchecked")
		List<? extends InputValidation> inputValidations = configuration.getInputValidations();

		if (inputValidations == null) {
			throw new InvalidConfigurationException("configuration does not contain element inputValidation");
		}

		for (InputValidation iv : inputValidations) {
			if (iv == null) {
				throw new InvalidConfigurationException("Encountered empty input parameter definition. Got a comma after the last object in the json file?");
			}
			String name = iv.getName();
			setPerformanceMarker("validateInput analyzing '" + name + "'");
			if (input.containsKey(name)) {
				Object data = input.get(name);
				checkInput(iv, name, data);
			}
			else {
				if (iv.isMandatory() != null && iv.isMandatory()) {
					throw new InputValidationException("Mandatory parameter '" + name + "' is missing");
				}
			}
		}
		setPerformanceMarker("process input done");
	}

	/**
	 * Convert string input parameters to correct types defined in Input Validations
	 *
	 * @param input A map of Strings. The RuleEngine will try to convert the strings to the datatype specified in the configuration.
	 * @throws InputValidationException If a parameter does not match the
	 *             validation rules, of the configuration an InputValidationException is
	 *             thrown containing information, what went wrong.
	 */
	private Map<String, Object> validateAndConvertInputMap(Map<String, String> input) throws InputValidationException {
		setPerformanceMarker("validate and convert input");
		Map<String, Object> convertedData = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		List<? extends InputValidation> inputValidations = configuration.getInputValidations();

		if (inputValidations == null) {
			throw new InvalidConfigurationException("configuration does not contain element inputValidation");
		}
		for (InputValidation iv : inputValidations) {
			if (iv == null) {
				throw new InvalidConfigurationException("Encountered empty input parameter definition. Got a comma after the last object in the json file?");
			}
			String key = iv.getName();
			String inputString = input.get(key);
			if (inputString == null) {
				continue;
			}
			switch (iv.getType()) {
				case "Code":
					convertedData.put(key, inputString);
					break;
				case "Integer":
					try {
						convertedData.put(key, Integer.valueOf(inputString));
					}
					catch (NumberFormatException e) {
						throw new InputValidationException("Cannot convert '" + inputString + "' to Integer for parameter '" + key + "'");
					}
					break;
				case "Double":
					try {
						convertedData.put(key, Double.valueOf(inputString));
					}
					catch (NumberFormatException e) {
						throw new InputValidationException("Cannot convert '" + inputString + "' to Double for parameter '" + key + "'");
					}
					break;
				case "String":
					convertedData.put(key, inputString);
					break;
				case "Boolean":
					if (!(StringUtils.equalsIgnoreCase("false", inputString) || StringUtils.equalsIgnoreCase("true", inputString))) {
						throw new InputValidationException(
								"Expected 'true' or 'false' for type Boolean, but got '" + inputString + "' for parameter '" + key + "'");
					}
					convertedData.put(key, Boolean.valueOf(inputString));
					break;
				default:
					convertedData.put(key, inputString);
					break;
			}
		}
		setPerformanceMarker("validate and convert input finished");
		return convertedData;
	}

	protected void resetDocument() {
		jsonDoc = this.configuration.getDocument();
	}

}
