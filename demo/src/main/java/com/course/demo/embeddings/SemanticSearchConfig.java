package com.course.demo.embeddings;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SemanticSearchConfig {
//    @Bean
//    @Primary
//    VectorStore semanticSearchVectorStore(EmbeddingModel embeddingModel){
//        return SimpleVectorStore.builder(embeddingModel).build();
//    }
}
