package cz.zajezdy.data.bengine.configuration;

import java.util.List;

public interface MultiDocument extends Document {

    List<? extends Document> getDocumentList();

}
