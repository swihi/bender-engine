package cz.zajezdy.data.bengine.test.configuration.model;

import cz.zajezdy.data.bengine.configuration.MultiDocument;

import java.util.List;

public class MultiTestDocument implements MultiDocument {
    private List<TestDocument> documentList;
    private Boolean canContinue = true;

    @Override
    public List<TestDocument> getDocumentList() {
        return documentList;
    }

    public Boolean getCanContinue() {
        return canContinue;
    }
}
