package com.course.demo.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persistent")
public class PersistentChatMemoryDemo {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ChatMemoryRepository chatMemoryRepository;

    public PersistentChatMemoryDemo(ChatClient.Builder builder,
                                    ChatMemoryRepository chatMemoryRepository) {
        this.chatClient = builder.build();
        this.chatMemoryRepository = chatMemoryRepository;

        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @GetMapping("/first")
    public String chatMemoryFirst(@RequestParam String conversationId ,
                                  @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a ->
                        a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }
    
    @GetMapping("/conversations")
    public List<String> getConversations(){
        return chatMemoryRepository.findConversationIds();
    }


    @GetMapping("/conversations/{conversationId}/messages")
    public List<ChatMessageView> getConversationMessages(@PathVariable String conversationId){
        return chatMemoryRepository.findByConversationId(conversationId)
                .stream()
                .map(ChatMessageView::from)
                .toList();
    }

    @DeleteMapping("/conversations/{conversationId}")
    public void deleteConversation(@PathVariable String conversationId){
         chatMemoryRepository.deleteByConversationId(conversationId);
    }

    @GetMapping("/users/{userId}/sessions/{sessionId}/chat")
    public String getConversationMessages(
            @PathVariable String userId, @PathVariable String sessionId){
        String conversationId = userId + ":" + sessionId;

        return chatClient.prompt()
                .user("message")
                .advisors(a ->
                        a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }
    
}
