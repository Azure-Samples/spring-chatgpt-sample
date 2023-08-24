package com.microsoft.azure.spring.chatgpt.sample.common;

import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.microsoft.azure.spring.chatgpt.sample.common.prompt.PromptTemplate;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.chatcompletion.ChatCompletion;
import com.microsoft.semantickernel.chatcompletion.ChatRequestSettings;
import com.microsoft.semantickernel.connectors.ai.openai.chatcompletion.OpenAIChatHistory;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class ChatSkill {

    private final Kernel kernel;

    public String chat(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("message shouldn't be empty.");
        }

        var lastUserMessage = messages.get(messages.size() - 1);
        if (lastUserMessage.getRole() != ChatRole.USER) {
            throw new IllegalArgumentException("The last message should be in user role.");
        }
        String question = lastUserMessage.getContent();

        // step 1. Query Top-K nearest text chunks from the vector store by embedding
        var searchResult = kernel.getMemory()
                .searchAsync("default", question, 5, 0.4, false)
                .block(Duration.ofSeconds(3))
                .stream()
                .map(ret -> ret.getMetadata().getText())
                .toList();

        // step 2. Populate the prompt template with the chunks
        var prompt = PromptTemplate.formatWithContext(searchResult, question);
        ChatCompletion<OpenAIChatHistory> chatCompletion = kernel.getService("chatGPT", ChatCompletion.class);
        var chatHistory = chatCompletion.createNewChat("You're a useful assistant.");
        for (int i = 0; i < messages.size() - 1; i++) {
            var msg = messages.get(i);
            if (msg.getRole().equals(ChatRole.USER)) {
                chatHistory.addUserMessage(msg.getContent());
            } else if (msg.getRole().equals(ChatRole.ASSISTANT)) {
                chatHistory.addAssistantMessage(msg.getContent());
            }
        }
        chatHistory.addUserMessage(prompt);

        // step 3. Call to OpenAI chat completion API
        return chatCompletion.generateMessageAsync(chatHistory, new ChatRequestSettings()).block();
    }
}
