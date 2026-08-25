package com.course.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                .user("Explain Spring Boot in simple terms.") // BUILD THE REQUEST
                .call() // SEND TO MODEL
                .content(); // READING THE RESPONSE
    }

//    SystemMessage
//    UserMessage
//    AssistantMessage
    // Message

    // Building conversation history
    // To set an example with AI model
    @GetMapping("/conversation-history")
    public String conversationHistory(@RequestParam String followUpQuestion) {
        Message systemMessage = new SystemMessage("You are a helpful Java Tutor");
        Message userMessage = new UserMessage("What is a Java Record?");
        Message priorReply = new AssistantMessage("A record is a compact way to declare an immutable data class in Java, "
                + "e.g. `record Point(int x, int y) {}`.");
        Message newUserMessage = new UserMessage(followUpQuestion);

        Prompt prompt = new Prompt(List.of(systemMessage,userMessage,
                priorReply,newUserMessage));

        return chatClient.prompt(prompt)
                .call()
                .content();
    }



}
