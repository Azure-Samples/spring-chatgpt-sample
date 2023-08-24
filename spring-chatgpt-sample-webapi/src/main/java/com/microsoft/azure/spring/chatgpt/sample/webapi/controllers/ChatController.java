package com.microsoft.azure.spring.chatgpt.sample.webapi.controllers;


import com.microsoft.azure.spring.chatgpt.sample.common.ChatSkill;
import com.microsoft.azure.spring.chatgpt.sample.webapi.models.ChatCompletionsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSkill chatSkill;

    @PostMapping("/completions")
    public String chatCompletion(@RequestBody ChatCompletionsRequest request) {
        return chatSkill.chat(request.getMessages());
    }
}
