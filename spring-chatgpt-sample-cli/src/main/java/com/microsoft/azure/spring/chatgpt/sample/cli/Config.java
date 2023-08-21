package com.microsoft.azure.spring.chatgpt.sample.cli;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.azure.spring.chatgpt.sample.common.AzureOpenAIClient;
import com.microsoft.azure.spring.chatgpt.sample.common.DocumentIndexPlanner;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.SimpleMemoryVectorStore;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.VectorStore;
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
    public DocumentIndexPlanner planner(AzureOpenAIClient openAIClient, VectorStore vectorStore) {
        return new DocumentIndexPlanner(openAIClient, vectorStore);
    }

    @Bean
    public AzureOpenAIClient azureOpenAIClient() {
        var innerClient = new OpenAIClientBuilder()
                .endpoint(openAiEndpoint)
                .credential(new AzureKeyCredential(openAiApiKey))
                .buildClient();
        return new AzureOpenAIClient(innerClient, embeddingDeploymentId, null);
    }

    @Bean
    @ConditionalOnProperty(name = "vector-store.type", havingValue = "memory", matchIfMissing = true)
    public VectorStore vectorStore() {
        return new SimpleMemoryVectorStore(vectorJsonFile);
    }
}
