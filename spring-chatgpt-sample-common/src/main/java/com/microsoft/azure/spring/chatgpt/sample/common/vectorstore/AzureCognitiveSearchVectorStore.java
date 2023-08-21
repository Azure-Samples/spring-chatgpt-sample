package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import com.azure.core.util.Context;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchQueryVector;
import com.azure.search.documents.util.SearchPagedIterable;

import java.util.List;
import java.util.stream.Collectors;

public class AzureCognitiveSearchVectorStore implements VectorStore {

    private final SearchClient searchClient;

    public AzureCognitiveSearchVectorStore(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    @Override
    public void saveDocument(DocEntry doc) {
        searchClient.uploadDocuments(List.of(doc));
    }

    @Override
    public DocEntry getDocument(String key) {
        return searchClient.getDocument(key, DocEntry.class);
    }

    @Override
    public void removeDocument(String key) {
        searchClient.deleteDocuments(List.of(DocEntry.builder().id(key).build()));
    }

    @Override
    public List<DocEntry> searchTopKNearest(List<Float> embedding, int k) {
        return searchTopKNearest(embedding, k, 0);
    }

    @Override
    public List<DocEntry> searchTopKNearest(List<Float> embedding, int k, double cutOff) {
        SearchQueryVector searchQueryVector = new SearchQueryVector()
                .setValue(embedding)
                .setKNearestNeighborsCount(k)
                .setFields("embedding");    // the field name from the class DocEntry

        SearchPagedIterable searchResults = searchClient.search(null,
                new SearchOptions().setVectors(searchQueryVector), Context.NONE);
        return searchResults.stream()
                .filter(r -> r.getScore() >= cutOff)
                .map(r -> r.getDocument(DocEntry.class))
                .collect(Collectors.toList());
    }

    @Override
    public void persist() {
        // do nothing
    }
}
