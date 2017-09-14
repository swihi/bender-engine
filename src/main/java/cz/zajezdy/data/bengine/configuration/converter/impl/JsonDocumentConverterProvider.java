package cz.zajezdy.data.bengine.configuration.converter.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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

				private String jsonDocument = null;

				@SuppressWarnings("rawtypes")
				public T fromJson(final String json) {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
							.registerTypeAdapter(BasicDocument.class, new JsonDeserializer<BasicDocument>() {

								@Override
								public BasicDocument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
									jsonDocument = json.toString();
									return null;
								}

							}).create();
					ParameterizedType type = TypeUtils.parameterize(BasicConfiguration.class, BasicDocument.class);
					T t = gson.fromJson(json, type);
					((Configuration) t).setJsonDocument(jsonDocument);
					return t;
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
