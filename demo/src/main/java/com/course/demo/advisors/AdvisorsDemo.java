package com.course.demo.advisors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/advisors")
public class AdvisorsDemo {

    private final ChatClient chatClient;

    public AdvisorsDemo(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @GetMapping(value = "/logging")
    public String loggingAdvisor(@RequestParam String topic) {
        SimpleLoggerAdvisor advisor = SimpleLoggerAdvisor.builder()
                .requestToString(request -> "SENDING PROMPT: "
                        + request.prompt().getContents())
                .responseToString(response -> "GOT BACK: "
                + response.getResult().getOutput().getText())
                .build();

        return chatClient.prompt()
                .user("Tell me a fun fact about this in 2 lines : " + topic)
//                .advisors(new SimpleLoggerAdvisor())
                .advisors(advisor)
                .call()
                .content();
    }

    @GetMapping(value = "/safeguard")
    public String safeguardAdvisor(@RequestParam String topic) {
        SafeGuardAdvisor safeGuardAdvisor = SafeGuardAdvisor.builder()
                .sensitiveWords(List.of("password", "credit card"))
                .failureResponse("I'm sorry, can't help you with that")
                .build();

        return chatClient.prompt()
                .user("Tell me a fun fact about this in 2 lines : " + topic)
//                .advisors(new SimpleLoggerAdvisor())
                .advisors(safeGuardAdvisor)
                .call()
                .content();
    }

//    1. persona    2. question
//    http://localhost:8080/api/advisors/persona?persona=senior%20engineer&question=what%20is%20java?
    // Custom advisor --> mutate the request

    @GetMapping(value = "/persona")
    public String persona(@RequestParam String persona,
                          @RequestParam String question) {
        return chatClient.prompt()
                .user(question)
//                .advisors(new SimpleLoggerAdvisor())
                .advisors(new PersonaAdvisor(persona, 0))
                .call()
                .content();
    }

    @GetMapping(value = "/disclaimer")
    public String disclaimer(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .advisors(new DisclaimerAdvisor(0))
                .call()
                .content();
    }

    @GetMapping(value = "/timing")
//    public String timing(@RequestParam String question) {
    public Flux<String> timing(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .advisors(new TimingAdvisor(0))
//                .call()
                .stream()
                .content();
    }
}
