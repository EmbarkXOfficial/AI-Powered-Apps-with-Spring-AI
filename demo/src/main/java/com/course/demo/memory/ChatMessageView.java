package com.course.demo.memory;

import org.springframework.ai.chat.messages.Message;

public record ChatMessageView(String role, String content) {
    public static ChatMessageView from(Message message) {
        return new ChatMessageView(message.getMessageType().getValue(),
                message.getText());
    }
}
