package com.course.demo.advisors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
