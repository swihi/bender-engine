package cz.zajezdy.data.bengine.configuration.converter.impl;

import java.lang.reflect.Type;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;


public class TypedConverterProvider<TDoc extends Document> implements JsonConverterProvider {

	private Type configurationType = null;
	private Type documentType = null;

	public void setConfigurationType(Type configurationType) {
		this.configurationType = configurationType;
	}

	public void setDocumentType(Type documentType) {
		this.documentType = documentType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> JsonConverter<T> getConverter(Class<T> interfaceType) {
		if (interfaceType == Configuration.class) {
			return (JsonConverter<T>) getConfigurationJsonConverter();
		}
		else if (interfaceType == Document.class) {
			return (JsonConverter<T>) getDocumentJsonConverter();
		}
		throw new RuntimeException("unsupported type for json converion");
	}

	public JsonConverter<BasicConfiguration> getConfigurationJsonConverter() {
		return new JsonConverter<BasicConfiguration>() {

			public BasicConfiguration fromJson(final String json) {
				return JsonHelper.fromJson(json, configurationType);
			}

			public String toJson(final BasicConfiguration object) {
				return JsonHelper.toJson(object);
			}
		};
	}

	public JsonConverter<TDoc> getDocumentJsonConverter() {
		return new JsonConverter<TDoc>() {

			@SuppressWarnings("unchecked")
			public TDoc fromJson(final String json) {
				Object o = JsonHelper.fromJson(json, documentType);
				return (TDoc) o;
			}

			public String toJson(final TDoc object) {
				return JsonHelper.toJson(object);
			}
		};
	}
}
