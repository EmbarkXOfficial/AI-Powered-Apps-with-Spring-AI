package com.course.demo.embeddings;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

//      1.0  →  same meaning
//      0.5  →  loosely related
//      0.0  →  completely unrelated

    @GetMapping("/similarity")
    public SimilarityResult similarity(@RequestParam String text1, @RequestParam String text2) {
        float[] vector1 = embeddingModel.embed(text1);
        float[] vector2 = embeddingModel.embed(text2);
        double similarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(vector1, vector2);
        return new SimilarityResult(text1, text2, similarity);
    }


}
