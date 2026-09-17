package com.course.demo.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memory")
public class ChatMemoryDemo {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatMemoryDemo(ChatClient.Builder builder) {
        this.chatClient = builder.build();

        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
    }

    @GetMapping("/first")
    public String chatMemoryFirst(@RequestParam String conversationId ,@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a ->
                        a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }
}
