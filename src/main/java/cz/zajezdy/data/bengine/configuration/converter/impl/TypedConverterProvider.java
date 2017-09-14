package cz.zajezdy.data.bengine.configuration.converter.impl;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;


public class TypedConverterProvider<TDoc extends Document> implements JsonConverterProvider {

	Type configurationType = null;
	Type documentType = null;

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

	private JsonConverter<BasicConfiguration<TDoc>> getConfigurationJsonConverter() {
		return new JsonConverter<BasicConfiguration<TDoc>>() {

			private String jsonDocument;

			public BasicConfiguration<TDoc> fromJson(final String json) {
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(documentType, new JsonDeserializer<Document>() {

					@Override
					public TDoc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
						jsonDocument = json.toString();
						return getDocumentJsonConverter().fromJson(jsonDocument);
					}

				}).create();
				BasicConfiguration<TDoc> t = gson.fromJson(json, configurationType);
				t.setJsonDocument(jsonDocument);
				return t;
			}

			public String toJson(final BasicConfiguration<TDoc> object) {
				return JsonHelper.toJson(object);
			}
		};
	}

	private JsonConverter<TDoc> getDocumentJsonConverter() {
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
