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
    private final ChatClient chatClientWithDefaultPersona;

    public TechAdvisorController(ChatClient.Builder builder) {
        this.chatClient = builder.build();

        this.chatClientWithDefaultPersona = builder
                .defaultSystem("You are a strict technical interviewer conducting a Java backend interview. "
                        + "Ask probing follow-up questions in your response.")
                .build();
    }

    @GetMapping
    public String advise() {
        return chatClient.prompt()
                .user("Explain Spring Boot in simple terms.") // BUILD THE REQUEST
                .call() // SEND TO MODEL
                .content(); // READING THE RESPONSE
    }

    @GetMapping("/default-system-prompt")
    public String defaultSystemPrompt(@RequestParam String question) {
        return chatClientWithDefaultPersona.prompt()
                .user(question) // BUILD THE REQUEST
                .call() // SEND TO MODEL
                .content(); // READING THE RESPONSE
    }

    @GetMapping("/system-prompt")
    public String systemPrompts(@RequestParam String persona,
                                @RequestParam String question) {

        String systemText = switch (persona.toLowerCase()) {
            case "pirate" -> "You are a pirate. Respond to everything in pirate speak, full of 'arrr' and nautical metaphors.";
            case "shakespeare" -> "You are William Shakespeare. Respond in early modern English.";
            case "interviewer" -> "You are a strict technical interviewer conducting a Java backend interview. "
                    + "Ask probing follow-up questions in your response.";
            default -> "You are a helpful assistant.";
        };

        return chatClient.prompt()
                .system(systemText)
                .user(question) // BUILD THE REQUEST
                .call() // SEND TO MODEL
                .content(); // READING THE RESPONSE
    }

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
