package com.course.demo.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;

public class PersonaAdvisor implements BaseAdvisor {

    String persona;
    int order;

    public PersonaAdvisor(String persona, int order) {
        this.persona = persona;
        this.order = order;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        Prompt augmented = chatClientRequest.prompt()
                .augmentSystemMessage("You are " + persona + ". Stay in character while answering" +
                        ". Answer about the topic in 2 lines max.");

        return chatClientRequest.mutate()
                .prompt(augmented)
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
