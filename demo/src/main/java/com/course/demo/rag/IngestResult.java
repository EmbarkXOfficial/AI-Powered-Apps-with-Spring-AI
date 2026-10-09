package com.course.demo.rag;

import java.util.List;

public record IngestResult(int documentsIndexed, List<String> titles) {
}
