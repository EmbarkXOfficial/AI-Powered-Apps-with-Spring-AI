package com.course.demo.embeddings;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/semantic")
public class SemanticSearchController {
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public SemanticSearchController(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    /*@PostConstruct
    void seed(){
        vectorStore.add(List.of(
                new Document("Spring AI provides a unified Java API for interacting with AI language models"),
                new Document("ChatClient is the main interface for sending prompts and receiving responses from AI"),
                new Document("Advisors in Spring AI intercept and modify prompts and responses in a chain"),
                new Document("VectorStore stores document embeddings for semantic similarity search"),
                new Document("EmbeddingModel converts text into high-dimensional numerical vectors called embeddings"),
                new Document("RAG stands for Retrieval Augmented Generation: retrieve relevant docs, then generate an answer"),
                new Document("ChatMemory stores conversation history to enable multi-turn conversations"),
                new Document("PromptTemplate allows parameterized prompt construction with named variables"),
                new Document("Structured output maps AI responses directly into Java records using entity()"),
                new Document("Streaming responses use Flux to deliver tokens to the client as they are generated"),
                new Document("SimpleVectorStore is an in-memory vector store backed by a ConcurrentHashMap"),
                new Document("Cosine similarity measures the angle between two vectors: 1.0 means identical, 0.0 means unrelated")
        ));
    }*/


    @PostConstruct
    void seed() {
        vectorStore.add(List.of(
                new Document("Spring AI provides a unified Java API for AI models",
                        Map.of("category", "framework")),
                new Document("ChatClient is the main interface for sending prompts to AI",
                        Map.of("category", "framework")),
                new Document("VectorStore stores document embeddings for semantic search",
                        Map.of("category", "framework")),
                new Document("Your invoice is generated on the 1st of every month",
                        Map.of("category", "billing")),
                new Document("You can cancel your subscription from account settings",
                        Map.of("category", "billing")),
                new Document("Refunds are processed within 5-7 business days",
                        Map.of("category", "billing")),
                new Document("To reset your password click forgot password on login",
                        Map.of("category", "account")),
                new Document("Two factor authentication can be enabled in security settings",
                        Map.of("category", "account"))
        ));
    }


    @PostMapping("/documents")
    public Map<String, Integer> addDocuments(@RequestBody List<String> texts) {
        List<Document> docs = texts.stream().map(Document::new).toList();
        vectorStore.add(docs);
        return Map.of("added", docs.size());
    }

    @GetMapping("/search")
    public List<DocumentResult> search(@RequestParam String query,
                                       @RequestParam(defaultValue = "4") int topK) {
        return vectorStore.similaritySearch(
                    SearchRequest.builder().query(query).topK(topK).build()
                ).stream()
                .map(
                        doc
                                -> new DocumentResult(doc.getId(),
                                doc.getText(),
                                doc.getScore()))
                .toList();
    }

    @GetMapping("/search/metadata")
    public List<DocumentResult> searchMetadata(@RequestParam String query,
                                       @RequestParam(defaultValue = "4") int topK,
                                               @RequestParam(required = false) String category) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(topK);

        if (category != null)
            builder.filterExpression("category == '" + category + "'");

        return vectorStore.similaritySearch(
                        builder.build()
                ).stream()
                .map(
                        doc
                                -> new DocumentResult(doc.getId(),
                                doc.getText(),
                                doc.getScore()))
                .toList();
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
