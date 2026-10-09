package com.course.demo.rag;

import org.springframework.ai.document.Document;

public record SearchResult(String title, String source, double score, String excerpt) {
    static SearchResult from(Document document) {
        String text = document.getText() != null ? document.getText() : "";
        String excerpt = text.length() > 300 ? text.substring(0, 300) + "..." : text;

        return new SearchResult(
                (String) document.getMetadata().getOrDefault("title", "Unknown"),
                (String) document.getMetadata().getOrDefault("source", "Unknown"),
                document.getScore() != null ? document.getScore() : 0.0,
                excerpt
        );
    }
}
