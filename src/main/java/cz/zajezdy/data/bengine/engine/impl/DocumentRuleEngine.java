package cz.zajezdy.data.bengine.engine.impl;

import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;


public class DocumentRuleEngine extends AbstractRuleEngine implements TypedRuleEngine<Document> {

	@Override
	public Document getDocument() {
		String doc = getJsonDocument();
		JsonConverter<Document> converter = getConverterProvider().getConverter(Document.class);
		Document d = converter.fromJson(doc);
		return d;
	}

}
