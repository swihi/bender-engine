package cz.zajezdy.data.bengine.test.configuration.model;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverterProvider;
import cz.zajezdy.data.bengine.configuration.converter.impl.JsonHelper;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;


public class ComputerConverterProvider implements JsonConverterProvider {

	@SuppressWarnings("rawtypes")
	private Type getClassType(final Class interfaceType) {
		if (interfaceType == Configuration.class) {
			return new TypeToken<BasicConfiguration>() {}.getType();
		}
		if (interfaceType == Document.class) {
			return new TypeToken<ComputerDocument>() {}.getType();
		}
		throw new UnsupportedOperationException("...");
	}

	public <T> JsonConverter<T> getConverter(final Class<T> interfaceType) {
		final Type classType = getClassType(interfaceType);

		return new JsonConverter<T>() {

			@SuppressWarnings("unchecked")
			public T fromJson(final String json) {
				return (T) JsonHelper.fromJson(json, classType);
			}

			public String toJson(final T object) {
				return JsonHelper.toJson(object);
			}
		};
	}
}
