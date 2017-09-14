package cz.zajezdy.data.bengine.configuration.converter;

public interface JsonConverterProvider {

	public <T> JsonConverter<T> getConverter(final Class<T> interfaceType);

}
