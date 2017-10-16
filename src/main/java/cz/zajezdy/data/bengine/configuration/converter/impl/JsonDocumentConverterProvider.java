package cz.zajezdy.data.bengine.configuration.converter.impl;

import com.google.gson.reflect.TypeToken;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;
import cz.zajezdy.data.bengine.configuration.impl.BasicDocument;


public class JsonDocumentConverterProvider implements JsonConverterProvider {

	@Override
	public <T> JsonConverter<T> getConverter(final Class<T> interfaceType) {
		if (interfaceType == Configuration.class) {
			return new JsonConverter<T>() {

				@SuppressWarnings("rawtypes")
				public T fromJson(final String json) {
					return JsonHelper.fromJson(json, BasicConfiguration.class);
				}

				public String toJson(final T object) {
					return JsonHelper.toJson(object);
				}
			};
		}
		else if (interfaceType == Document.class) {
			return new JsonConverter<T>() {

				@SuppressWarnings("unchecked")
				public T fromJson(final String json) {
					return (T) JsonHelper.fromJson(json, new TypeToken<BasicDocument>() {}.getType());
				}

				public String toJson(final T object) {
					return JsonHelper.toJson(object);
				}
			};

		}

		throw new RuntimeException("unsupported type");
	}

}
