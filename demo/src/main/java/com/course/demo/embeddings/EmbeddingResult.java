package com.course.demo.embeddings;

public record EmbeddingResult(String text, int dimensions, float[] vector) {
}
