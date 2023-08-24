package com.microsoft.azure.spring.chatgpt.sample.cli;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.azure.spring.chatgpt.sample.common.DocumentIndexSkill;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.PersistentMemoryStore;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${azure.openai.embedding-deployment-id}")
    private String embeddingDeploymentId;

    @Value("${azure.openai.endpoint}")
    private String openAiEndpoint;

    @Value("${azure.openai.api-key}")
    private String openAiApiKey;

    @Value("${vector-store.file:}")
    private String vectorJsonFile;

    @Bean
    public Kernel kernel(PersistentMemoryStore memoryStore) {
        var client = new OpenAIClientBuilder()
                .endpoint(openAiEndpoint)
                .credential(new AzureKeyCredential(openAiApiKey))
                .buildAsyncClient();
        return SKBuilders.kernel()
                .withDefaultAIService(SKBuilders.textEmbeddingGenerationService()
                        .withOpenAIClient(client)
                        .setModelId(embeddingDeploymentId)
                        .build())
                .withMemoryStorage(memoryStore)
                .build();
    }

    @Bean
    public DocumentIndexSkill documentIndex(Kernel kernel, PersistentMemoryStore memoryStore) {
        return new DocumentIndexSkill(kernel, memoryStore);
    }

    @Bean
    @ConditionalOnProperty(name = "vector-store.type", havingValue = "memory", matchIfMissing = true)
    public PersistentMemoryStore vectorStore() {
        return new PersistentMemoryStore(vectorJsonFile);
    }
}
