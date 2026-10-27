package com.course.demo.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    private static final String RAG_PROMPT = """
        You are a helpful assistant. Answer the question using ONLY the information provided \
        in the context below. If the answer is not in the context, say \
        "I don't have that information in my knowledge base."
        Be concise and factual.

        Context:
        ---
        {context}
        ---

        Question: {question}
        """;

    public RagController(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    private String extractMetaValue(String content, String prefix) {
        return Arrays.stream(content.split("\n"))
                .filter(line -> line.startsWith(prefix))
                .findFirst()
                .map(line -> line.substring(prefix.length()).trim())
                .orElse("unknown");
    }

    @PostMapping("/ingest")
    @PostConstruct
    public IngestResult ingest() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/rag/*.txt");
        List<Document> documents = Arrays.stream(resources)
                .map(resource -> {
                    try {

                        String content = resource.getContentAsString(StandardCharsets.UTF_8);
                        String source = extractMetaValue(content, "SOURCE:");
                        String title = extractMetaValue(content, "TITLE:");
                        String filename = resource.getFilename() != null ? resource.getFilename() : "unknown";
                        String id = UUID.nameUUIDFromBytes(filename.getBytes(StandardCharsets.UTF_8)).toString();

                        return Document.builder()
                                .id(id)
                                .text(content)
                                .metadata(Map.of("source", source, "title", title, "file", filename))
                                .build();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        vectorStore.add(documents);
        return new IngestResult(documents.size(), documents.stream()
                .map(d -> (String) d.getMetadata().get("title")).toList());
    }


    @GetMapping("/search")
    public List<SearchResult> search(@RequestParam String query,
                                     @RequestParam(defaultValue = "3") String topK) {
        return vectorStore.similaritySearch(
                        SearchRequest.builder().query(query)
                                .topK(Integer.parseInt(topK)).build()
                ).stream()
                .map(SearchResult::from).toList();
    }

    //    BUILD 2: RAG ANSWER WITH CITATIONS
    @GetMapping("/ask")
    public RagAnswer ask(@RequestParam String question,
                         @RequestParam(defaultValue = "3") String topK) {
        // 1. Retrieve most relevant documents
        List<Document> contextDocs = vectorStore.similaritySearch(
                        SearchRequest.builder().query(question)
                                .topK(Integer.parseInt(topK)).build());

        // 2. Build the context
        String context = contextDocs.stream()
                .map(d -> "[" + d.getMetadata().get("title") + "]\n" + d.getText())
                .reduce((a, b) -> a + "\n\n --- \n\n" + b)
                .orElse("No relevant documents found");

        // 3. Call the model with grounded prompt
        String answer = chatClient.prompt()
                .user(u -> u.text(RAG_PROMPT)
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();

        // 4. Build a citation list and the answer
        List<Citation> citations = contextDocs.stream()
                .map(Citation::from)
                .toList();

        return new RagAnswer(question, answer, citations);
    }
}
