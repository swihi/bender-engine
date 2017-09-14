package cz.zajezdy.data.bengine.configuration.converter;

import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;


// TODO change interface to this one
@SuppressWarnings("rawtypes")
public interface JsonConverterProviderNew {

	public <TConf extends Configuration> JsonConverter<TConf> getConfigurationConverter();

	public <TDoc extends Document> JsonConverter<TDoc> getDocumentConverter();

}
