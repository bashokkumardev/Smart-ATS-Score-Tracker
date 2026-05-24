package com.ashok.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;

import com.ashok.jobtracker.service.AtsScoringEngine;
import com.ashok.jobtracker.service.AtsScoringEngine.AtsScoringResult;
import org.junit.jupiter.api.Test;

class AtsScoringEngineTest {

    private final AtsScoringEngine engine = new AtsScoringEngine();

    @Test
    void score_matchesPhrasesNotRandomTokens() {
        String resume = "Java developer with Spring Boot, MongoDB, REST API, Git, microservices experience.";
        String jobDescription =
                "Strong proficiency in Java and Spring Boot. Experience with Kafka, Python, REST API required.";

        AtsScoringResult result = engine.score(resume, jobDescription);

        assertThat(result.score()).isGreaterThan(0);
        assertThat(result.matchedSkills()).contains("java", "spring boot", "rest api");
        assertThat(result.missingSkills()).contains("kafka", "python");
        assertThat(result.recommendations()).isNotEmpty();
        assertThat(result.summary()).contains("matched");
    }

    @Test
    void score_doesNotTreatJavascriptAsJava() {
        AtsScoringResult result = engine.score("JavaScript and TypeScript developer", "Java required");

        assertThat(result.matchedSkills()).doesNotContain("java");
    }
}
