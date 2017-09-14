package cz.zajezdy.data.bengine.configuration.converter;


public interface JsonConverter<T> {

    public T fromJson(String json);

    public String toJson(T object);
}
