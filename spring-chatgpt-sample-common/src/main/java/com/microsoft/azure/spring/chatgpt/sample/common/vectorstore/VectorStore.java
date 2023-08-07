package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import java.util.List;

public interface VectorStore {
    void saveDocument(DocEntry doc);

    DocEntry getDocument(String key);

    void removeDocument(String key);

    List<DocEntry> searchTopKNearest(List<Float> embedding, int k);

    List<DocEntry> searchTopKNearest(List<Float> embedding, int k, double cutOff);

    void persist();
}
