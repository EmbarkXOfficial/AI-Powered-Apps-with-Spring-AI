package com.course.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advisor")
public class TechAdvisorController {
    private final ChatClient chatClient;

    public TechAdvisorController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String advise() {
        return chatClient.prompt()
                .user("Explain Spring Boot in simple terms.")
                .call()
                .content();
    }
}
