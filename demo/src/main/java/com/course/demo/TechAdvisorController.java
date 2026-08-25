package com.course.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advisor")
public class TechAdvisorController {
    private final ChatClient chatClient;
    private final ChatClient chatClientWithDefaultPersona;

    @Value("classpath:/prompts/recipe-prompt.st")
    private Resource recipePrompt;
//    Dear [Name], your appointment on [Date] at [Time] has been confirmed.

    public TechAdvisorController(ChatClient.Builder builder) {
        this.chatClient = builder.build();

        this.chatClientWithDefaultPersona = builder.clone()
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

    @GetMapping("/prompt-variables")
    public String promptVariables(@RequestParam String language,
                                  @RequestParam String task){
        PromptTemplate promptTemplate = new PromptTemplate("""
                Write a short {language} code snippet that demonstrates how to {task}.
                Include comments explaining each step.
                """);

        Prompt prompt = promptTemplate.create(Map.of(
                "language",language,
                "task", task
        ));

        return "<pre>" + chatClient.prompt(prompt)
                .call()
                .content() + "<pre>";
    }

    @GetMapping("/inline-prompt-templates")
    public String inlinePromptTemplates(@RequestParam String language,
                                  @RequestParam String task){
//        PromptTemplate promptTemplate = new PromptTemplate("""
//                Write a short {language} code snippet that demonstrates how to {task}.
//                Include comments explaining each step.
//                """);
//
//        Prompt prompt = promptTemplate.create(Map.of(
//                "language",language,
//                "task", task
//        ));

        return "<pre>" + chatClient.prompt()
                .user(u -> u.text("""
                Write a short {language} code snippet that demonstrates how to {task}.
                Include comments explaining each step.
                """)
                        .param("language",language)
                        .param("task", task))
                .call()
                .content() + "<pre>";
    }

    @GetMapping("/external-prompt-file")
    public String externalPromptFile(@RequestParam String ingredient){
        PromptTemplate promptTemplate = new PromptTemplate(recipePrompt);
        Prompt prompt = promptTemplate.create(
                Map.of("ingredient",ingredient)
        );

        return "<pre>" + chatClient.prompt(prompt)
                .call()
                .content() + "<pre>";
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
