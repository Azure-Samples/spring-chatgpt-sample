package com.microsoft.azure.spring.chatgpt.sample.webapi;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.azure.spring.chatgpt.sample.common.ChatSkill;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.PersistentMemoryStore;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.chatcompletion.ChatCompletion;
import com.microsoft.semantickernel.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Value("${azure.openai.embedding-deployment-id}")
    private String embeddingDeploymentId;

    @Value("${azure.openai.chat-deployment-id}")
    private String chatDeploymentId;

    @Value("${azure.openai.endpoint}")
    private String endpoint;

    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Value("${vector-store.file}")
    private String vectorJsonFile;

    @Bean
    public ChatSkill chat(Kernel kernel) {
        return new ChatSkill(kernel);
    }

    @Bean
    public Kernel kernel(MemoryStore memoryStore) {
        var client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildAsyncClient();
        return SKBuilders.kernel()
                .withAIService("chatGPT",
                        SKBuilders.chatCompletion()
                                .withOpenAIClient(client)
                                .setModelId(chatDeploymentId)
                                .build(),
                        true,
                        ChatCompletion.class)
                .withDefaultAIService(SKBuilders.textEmbeddingGenerationService()
                        .withOpenAIClient(client)
                        .setModelId(embeddingDeploymentId)
                        .build())
                .withMemoryStorage(memoryStore)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "vector-store.type", havingValue = "memory", matchIfMissing = true)
    public MemoryStore memoryStore() {
        return PersistentMemoryStore.loadFromFile(vectorJsonFile);
    }
}
