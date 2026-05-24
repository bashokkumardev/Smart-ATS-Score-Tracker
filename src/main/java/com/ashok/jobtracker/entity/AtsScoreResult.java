package com.ashok.jobtracker.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ats_score_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsScoreResult {

    @Id
    private String id;

    @Indexed
    private String userId;

    private Integer score;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    @Builder.Default
    private List<String> recommendations = new ArrayList<>();

    private String summary;

    private String jobDescription;

    private String jobTitle;

    private String companyName;

    private String resumeFileName;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
