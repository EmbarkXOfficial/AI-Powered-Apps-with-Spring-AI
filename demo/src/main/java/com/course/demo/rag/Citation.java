package com.course.demo.rag;

import org.springframework.ai.document.Document;

public record Citation(String title, String source, double score, String excerpt) {
    static Citation from(Document document) {
        String text = document.getText() != null ? document.getText() : "";
        String excerpt = text.length() > 200 ? text.substring(0, 200) + "..." : text;

        return new Citation(
                (String) document.getMetadata().getOrDefault("title", "Unknown"),
                (String) document.getMetadata().getOrDefault("source", "Unknown"),
                document.getScore() != null ? document.getScore() : 0.0,
                excerpt
        );
    }
}
