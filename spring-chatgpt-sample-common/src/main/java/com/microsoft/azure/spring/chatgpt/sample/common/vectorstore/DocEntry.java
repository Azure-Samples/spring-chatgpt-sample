package com.microsoft.azure.spring.chatgpt.sample.common.vectorstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocEntry {
    private String id;

    private String hash;

    private String text;

    private List<Double> embedding;
}
