package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.semantickernel.memory.MemoryRecord;
import com.microsoft.semantickernel.memory.VolatileMemoryStore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentMemoryStore extends VolatileMemoryStore {

    private final String persistFilePath;

    public PersistentMemoryStore(String persistFilePath) {
        this.persistFilePath = persistFilePath;
    }

    public static PersistentMemoryStore loadFromFile(String filePath) {
        var memoryStore = new PersistentMemoryStore(filePath);
        var reader = new ObjectMapper().readerFor(new TypeReference<Map<String, Collection<MemoryRecord>>>() {  });
        try {
            Map<String, Collection<MemoryRecord>> data = reader.readValue(new File(filePath));
            for (var collection : data.entrySet()) {
                String collectionName = collection.getKey();
                memoryStore.createCollectionAsync(collectionName).block();
                memoryStore.upsertBatchAsync(collectionName, collection.getValue()).block();
            }
            return memoryStore;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveToFile() {
        if (persistFilePath.isBlank()) {
            throw new IllegalStateException("persistFilePath shouldn't be empty.");
        }

        var data = new HashMap<String, Collection<MemoryRecord>>();
        var collectionNames = this.getCollectionsAsync().blockOptional().orElse(List.of());
        for (var collectionName: collectionNames) {
            var records = this.getCollection(collectionName);
            data.put(collectionName, records.values());
        }

        var objectWriter = new ObjectMapper()
                .addMixIn(MemoryRecord.class, JsonSerializationMixIn.class)
                .writer()
                .withDefaultPrettyPrinter();
        try (var fileWriter = new FileWriter(persistFilePath, StandardCharsets.UTF_8)) {
            objectWriter.writeValue(fileWriter, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private abstract class JsonSerializationMixIn {
        @JsonIgnore
        abstract String getSerializedMetadata() throws JsonProcessingException;

        @JsonIgnore
        abstract String getSerializedEmbedding() throws JsonProcessingException;
    }
}
