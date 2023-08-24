package com.microsoft.azure.spring.chatgpt.sample.common;

import com.microsoft.azure.spring.chatgpt.sample.common.reader.SimpleFolderReader;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.PersistentMemoryStore;
import com.microsoft.semantickernel.Kernel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class DocumentIndexSkill {

    private final Kernel kernel;

    private static final String COLLECTION_NAME = "default";

    private final PersistentMemoryStore memoryStore;

    public void buildFromFolder(String folderPath) throws IOException {
        if (folderPath == null) {
            throw new IllegalArgumentException("folderPath shouldn't be empty.");
        }

        SimpleFolderReader reader = new SimpleFolderReader(folderPath);
        TextSplitter splitter = new TextSplitter();
        var textMemory = kernel.getMemory();
        reader.run((fileName, content) -> {
            log.info("String to process {}...", fileName);
            var textChunks = splitter.split(content);
            for (var chunk: textChunks) {
                String id = UUID.randomUUID().toString();
                id = textMemory.saveInformationAsync(
                        COLLECTION_NAME, chunk, id, fileName, null).block();
                log.info("Saved document {}", id);
            }
            return null;
        });

        memoryStore.saveToFile();

        log.info("All documents are loaded to the vector store.");
    }
}
