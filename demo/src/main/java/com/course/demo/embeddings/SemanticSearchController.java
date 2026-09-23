package com.course.demo.embeddings;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semantic")
public class SemanticSearchController {
    private final EmbeddingModel embeddingModel;

    public SemanticSearchController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }


    @GetMapping("/embed")
    public EmbeddingResult embed(String text) {
        float[] vector = embeddingModel.embed(text);
        return new EmbeddingResult(text, vector.length, vector);
    }
}
