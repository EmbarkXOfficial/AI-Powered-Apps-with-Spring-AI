package com.course.demo.structured;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/structured")
public class StructuredDemo {

    /*
    * Resume Parsing
    * Quiz Generation
    * Invoice / Metadata extraction
    *
    * */

    private final ChatClient chatClient;

    @Value("classpath:/prompts/few-shot.st")
    private Resource fewShotPrompt;

    public StructuredDemo(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @GetMapping("/course-summary")
    public CourseSummary courseSummary(@RequestParam String topic) {
        String prompt = """
                Create a course summary for a beginner-friendly course on "%s".
                Include a title, the difficulty level, a list of 4-6 learning objectives,
                and the estimated number of hours to complete the course.
                """.formatted(topic);
/*        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CourseSummary.class);*/

        /*
        *  BeanOutputConverter
           ListOutputConverter
           MapOutputConverter
        *
        * */

        CourseSummary courseSummary = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CourseSummary.class);

        System.out.printf("COURSE SUMMARY: " + courseSummary.title());
        return courseSummary;
    }

}
