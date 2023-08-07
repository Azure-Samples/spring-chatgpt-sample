package com.microsoft.azure.spring.chatgpt.sample.cli;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.microsoft.azure.spring.chatgpt.sample.common.AzureOpenAIClient;
import com.microsoft.azure.spring.chatgpt.sample.common.DocumentIndexPlanner;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.AzureCognitiveSearchVectorStore;
import com.microsoft.azure.spring.chatgpt.sample.common.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${azure.cognitive-search.endpoint}")
    private String acsEndpoint;

    @Value("${azure.cognitive-search.api-key}")
    private String acsApiKey;

    @Value("${azure.cognitive-search.index}")
    private String acsIndexName;


    @Bean
    public DocumentIndexPlanner planner(AzureOpenAIClient openAIClient, VectorStore vectorStore) {
        return new DocumentIndexPlanner(openAIClient, vectorStore);
    }

    @Bean
    public AzureOpenAIClient AzureOpenAIClient() {
        var innerClient = new OpenAIClientBuilder()
                .endpoint(openAiEndpoint)
                .credential(new AzureKeyCredential(openAiApiKey))
                .buildClient();
        return new AzureOpenAIClient(innerClient, embeddingDeploymentId, null);
    }

//    @Bean
//    public VectorStore vectorStore() {
//        return new SimpleMemoryVectorStore(vectorJsonFile);
//    }

    @Bean
    public VectorStore vectorStore() {
        final SearchClient searchClient = new SearchClientBuilder()
                .endpoint(openAiEndpoint)
                .credential(new AzureKeyCredential(acsApiKey))
                .indexName(acsIndexName)
                .buildClient();
        return new AzureCognitiveSearchVectorStore(searchClient);
    }
}
