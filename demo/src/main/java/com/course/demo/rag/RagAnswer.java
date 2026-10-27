package com.course.demo.rag;

import java.util.List;

public record RagAnswer(String question, String answer, List<Citation> citations) {
}
