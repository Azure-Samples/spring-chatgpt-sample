package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleMemoryVectorStore implements VectorStore {

    private final VectorStoreData data;

    @Setter
    @Getter
    private String persistFilePath;

    public SimpleMemoryVectorStore() {
        this.data = new VectorStoreData();
    }

    public SimpleMemoryVectorStore(String persistFilePath) {
        if (persistFilePath.isBlank()) {
            throw new IllegalArgumentException("persistFilePath shouldn't be empty.");
        }
        this.data = new VectorStoreData();
        this.persistFilePath = persistFilePath;
    }

    SimpleMemoryVectorStore(String persistFilePath, VectorStoreData data) {
        if (persistFilePath.isBlank()) {
            throw new IllegalArgumentException("persistFilePath shouldn't be empty.");
        }
        this.persistFilePath = persistFilePath;
        this.data = data;
    }

    @Override
    public void saveDocument(DocEntry doc) {
        data.store.put(doc.getId(), doc);
    }

    @Override
    public DocEntry getDocument(String key) {
        return data.store.getOrDefault(key, null);
    }

    @Override
    public void removeDocument(String key) {
        data.store.remove(key);
    }

    @Override
    public List<DocEntry> searchTopKNearest(List<Float> embedding, int k) {
        return searchTopKNearest(embedding, k, 0);
    }

    @Override
    public List<DocEntry> searchTopKNearest(List<Float> embedding, int k, double cutOff) {
        var similarities = data.store.values().stream().map(entry -> new Similarity(
                        entry.getId(),
                        EmbeddingMath.cosineSimilarity(embedding, entry.getEmbedding())));
        var docs = similarities.filter(s -> s.similarity >= cutOff)
                .sorted(Comparator.<Similarity>comparingDouble(s -> s.similarity).reversed())
                .limit(k)
                .map(s -> data.store.get(s.key))
                .toList();
        return docs;
    }

    public void persist() {
        if (persistFilePath.isBlank()) {
            throw new IllegalStateException("persistFilePath shouldn't be empty.");
        }
        var objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try (var fileWriter = new FileWriter(persistFilePath, StandardCharsets.UTF_8)) {
            objectWriter.writeValue(fileWriter, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SimpleMemoryVectorStore loadFromJsonFile(String filePath) {
        var reader = new ObjectMapper().reader();
        try {
            var data = reader.readValue(new File(filePath), VectorStoreData.class);
            return new SimpleMemoryVectorStore(filePath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AllArgsConstructor
    private static class Similarity {
        private String key;
        private double similarity;
    }

    @Setter
    @Getter
    private static class VectorStoreData {
        private Map<String, DocEntry> store = new ConcurrentHashMap<>();
    }
}
