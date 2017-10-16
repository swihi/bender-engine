package cz.zajezdy.data.bengine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.impl.TypedConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;
import cz.zajezdy.data.bengine.test.configuration.model.TestDocument;
import cz.zajezdy.data.bengine.test.util.ResourceFileHelper;


public class ConverterTest {

	private String getTestDocJson() {
		return "{ 'test' : false, 'testText' : 'hello' }";
	}

	private String getTestConfigJson() {
		return ResourceFileHelper.getFileContentNoException("testconfig.json");
	}

	@Test
	public void test() {
		TypedConverterProvider<TestDocument> prov = new TypedConverterProvider<TestDocument>();
		prov.setConfigurationType(BasicConfiguration.class);
		prov.setDocumentType(TestDocument.class);
		@SuppressWarnings("rawtypes")
		JsonConverter<Configuration> configConverter = prov.getConverter(Configuration.class);
		JsonConverter<Document> docConverter = prov.getConverter(Document.class);
		TestDocument testDoc = (TestDocument) docConverter.fromJson(getTestDocJson());
		assertFalse(testDoc.getTest());
		assertEquals("hello", testDoc.getTestText());

		@SuppressWarnings("unchecked")
		BasicConfiguration testConfig = (BasicConfiguration) configConverter.fromJson(getTestConfigJson());
		assertEquals("1.1", testConfig.getVersion());
		String testDoc2 = testConfig.getDocument();
		assertEquals("{\"test\":false,\"expensive\":false,\"testText\":\"no clue\",\"testValue\":5.0,\"inject\":\"none\"}", testDoc2);
	}
}
