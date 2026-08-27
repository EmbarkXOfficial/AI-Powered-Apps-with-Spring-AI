package com.course.demo.structured;

import java.util.List;

public record CourseSummary (
    String title,
    String level,
    List<String> learningObjectives,
    int estimatedHours
) {

}
