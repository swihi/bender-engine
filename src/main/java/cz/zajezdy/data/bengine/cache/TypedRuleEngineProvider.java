package cz.zajezdy.data.bengine.cache;

import cz.zajezdy.data.bengine.TypedRuleEngine;
import cz.zajezdy.data.bengine.builder.RuleEngineBuilder;
import cz.zajezdy.data.bengine.configuration.Configuration;
import cz.zajezdy.data.bengine.configuration.Document;
import cz.zajezdy.data.bengine.configuration.converter.JsonConverter;
import cz.zajezdy.data.bengine.configuration.converter.impl.TypedConverterProvider;
import cz.zajezdy.data.bengine.configuration.impl.BasicConfiguration;

import java.io.IOException;

public abstract class TypedRuleEngineProvider<TDoc extends Document> implements RuleEngineProvider {
    private final TypedConverterProvider<TDoc> converterProvider;
    private Class<TDoc> documentClass;

    protected TypedRuleEngineProvider() {
        this.converterProvider = new TypedConverterProvider<>();
    }

    public void setDocumentType(Class<TDoc> clazz) {
        this.converterProvider.setDocumentType(clazz);
        this.documentClass = clazz;
    }

    public Class<TDoc> getDocumentClass() {
        return this.documentClass;
    }

    @Override
    public Configuration<TDoc> deserialize(String json) {
        final JsonConverter<BasicConfiguration<TDoc>> converter = converterProvider.getConfigurationJsonConverter();
        return converter.fromJson(json);
    }

    @Override
    public abstract String getConfigurationContent(String filename) throws IOException;

    @Override
    public TypedRuleEngine<TDoc> getEngine(Configuration configuration) {
        if (documentClass == null) {
            return null;
        }
        converterProvider.setConfigurationType(configuration.getClass());
        RuleEngineBuilder reb = new RuleEngineBuilder();
        reb.withConfiguration(configuration);
        reb.withJsonConverterProvider(converterProvider);
        TypedRuleEngine<TDoc> ruleEngine = reb.buildTyped(documentClass);
        configureEngine(ruleEngine);
        return ruleEngine;
    }

    public abstract void configureEngine(TypedRuleEngine<TDoc> ruleEngine);
}
