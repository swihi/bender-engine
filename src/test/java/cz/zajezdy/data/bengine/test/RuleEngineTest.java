package cz.zajezdy.data.bengine.test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import cz.zajezdy.data.bengine.RuleEngine;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.converter.impl.BasicConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;
import cz.zajezdy.data.bengine.configuration.impl.BasicDocument;
import cz.zajezdy.data.bengine.test.util.ExecutionHelper;
import cz.zajezdy.data.bengine.test.util.InputHelper;
import cz.zajezdy.data.bengine.test.util.ResourceFileHelper;
import cz.zajezdy.data.bengine.test.util.TestHelper;

import static org.junit.Assert.*;


public class RuleEngineTest {

	public static boolean logActionExecuted = false;

	@Test
	public void testWithDoc() throws Exception {
		ExecutionHelper.execEngineWithTestDocument("testconfig.json", InputHelper.getTestDocumentInput());
	}

//  To get rid of locking of RuleEngines in RuleEngineCache input paramaters are passed directly to executeMethod,
//  so this test is obsolete
//	@Test
//	public void testMultipleExecution() throws Exception {
//		TypedRuleEngine<TestDocument> re = TestHelper.buildTestDocEngine("testconfig.json");
//		Map<String, Object> input = InputHelper.getTestDocumentInput();
//
//		ExecutionHelper.execEngineWithTestDocument(re, input);
//
//		input.put("input2", "testXX");
//		ExecutionHelper.execEngineWithTestDocument(re, input);
//	}

	@Test
	public void testWithoutDoc() throws Exception {
		RuleEngine re = TestHelper.buildEngineWithoutDoc("testconfig.json");
		String json1 = re.getJsonDocument();
        // TODO unfortunately Gson deserializes integers as double. It would be better use Jackson instead
		String expected1 = "{\"test\":false,\"expensive\":false,\"testText\":\"no clue\",\"testValue\":5.0,\"inject\":\"none\"}";
		assertEquals(expected1, json1);
		ExecutionHelper.execEngine(re, InputHelper.getTestDocumentInput());
		String json2 = re.getJsonDocument();
		String expected2 = "{\"test\":true,\"expensive\":true,\"testText\":\"Wow... two input parameters have the same value ;)\",\"testValue\":24,\"inject\":\"none\",\"text\":\"I am really expensive\",\"textInfo\":\"Someone has set document.text\"}";
		assertEquals(expected2, json2);
	}

	@Test
	public void testDocDesirialization() throws IOException, ScriptException {
		String json = ResourceFileHelper.getFileContentNoException("testconfig.json");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
				.registerTypeAdapter(BasicDocument.class, new JsonDeserializer<BasicDocument>() {

					@Override
					public BasicDocument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
						// System.out.println(json);
						return null;
					}

				}).create();
		@SuppressWarnings("unchecked")
		BasicConfiguration conf = gson.fromJson(json, BasicConfiguration.class);
		assertNotNull(conf);
	}

	@Test
	public void testConverter() throws IOException {
		String json = ResourceFileHelper.getFileContent("testconfig.json");
		// System.out.println(json);

		JsonConverterProvider converter = new BasicConverterProvider();
		@SuppressWarnings("rawtypes")
		JsonConverter<? extends Configuration> cv = converter.getConverter(Configuration.class);
		@SuppressWarnings("rawtypes")
		Configuration c = cv.fromJson(json);
		@SuppressWarnings("unchecked")
		BasicConfiguration dc = (BasicConfiguration) c;
		System.out.println(dc.getVersion());
		String document = dc.getDocument();
		assertNotNull(document);
		assertNotEquals("", document);
	}

	@Test
	public void testReflectionInputType() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		input.put("decimal", new BigDecimal(99));
		ExecutionHelper.execEngineWithTestDocument("testconfig.json", input);
	}

	@Test
	public void testBoolean() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		input.put("bool", new Boolean(true));
		ExecutionHelper.execEngineWithTestDocument("testconfig.json", input);
	}

	@Test
	public void testEscaping() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		input.put("textInput", "test1'");
		input.put("input2", "test1'");
		ExecutionHelper.execEngineWithTestDocument("testconfig.json", input);
	}

	@Test
	public void testExpressionValidation() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		input.put("textInput", "hallo");
		ExecutionHelper.execEngineWithTestDocumentInputValidationError("testconfig.json", input, "textInput");
	}

	@Test
	public void testBooleanAllowedValues() throws Exception {
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		input.put("bool", new Boolean(false));
		ExecutionHelper.execEngineWithTestDocumentInputValidationError("testconfig.json", input, "bool");
	}

	@Test
	public void testLogAction() throws Exception {
		logActionExecuted = false;
		Map<String, Object> input = InputHelper.getTestDocumentInput();
		ExecutionHelper.execEngineWithTestDocument("testconfig.json", input);
		assertTrue(logActionExecuted);
	}

//  To get rid of locking of RuleEngines in RuleEngineCache input paramaters are passed directly to executeMethod,
//  so this test is obsolete
//	@Test
//	public void testChangeInput() throws Exception {
//		logActionExecuted = false;
//		Map<String, Object> input = InputHelper.getTestDocumentInput();
//		TypedRuleEngine<TestDocument> re = TestHelper.buildTestDocEngine("testconfig.json");
//		ExecutionHelper.execEngineWithTestDocument(re, input);
//		@SuppressWarnings("unchecked")
//		HashMap<String, Object> changedInput = new Gson().fromJson(re.getJsonInput(), HashMap.class);
//		assertEquals("world", input.get("changingString"));
//	}

	// TODO: check if code injection can and shall be reactivated
	// @Test
	// public void testCodeType() throws Exception {
	// logActionExecuted = false;
	// Map<String, Object> input = InputHelper.getTestDocumentInput();
	// input.put("code", "var xyz = function() { return 'hello world'; }");
	// String jsonDoc =
	// ExecutionHelper.execEngineWithTestDocument("testconfig.json", input);
	// assertTrue(jsonDoc.contains("hello world"));
	// }
}
