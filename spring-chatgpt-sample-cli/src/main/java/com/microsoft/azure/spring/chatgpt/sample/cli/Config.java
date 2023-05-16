package com.microsoft.azure.spring.chatgpt.sample.cli;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.azure.spring.chatgpt.sample.common.AzureOpenAIClient;
import com.microsoft.azure.spring.chatgpt.sample.common.DocumentIndexPlanner;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.SimpleMemoryVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${azure.openai.embedding-deployment-id}")
    private String embeddingDeploymentId;

    @Value("${azure.openai.endpoint}")
    private String endpoint;

    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Bean
    public DocumentIndexPlanner planner(AzureOpenAIClient openAIClient, SimpleMemoryVectorStore vectorStore) {
        return new DocumentIndexPlanner(openAIClient, vectorStore);
    }

    @Bean
    public AzureOpenAIClient AzureOpenAIClient() {
        var innerClient = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();
        return new AzureOpenAIClient(innerClient, embeddingDeploymentId, null);
    }

    @Bean
    public SimpleMemoryVectorStore vectorStore() {
        return new SimpleMemoryVectorStore();
    }
}
