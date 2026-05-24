package com.ashok.jobtracker.dto.ats;

import com.ashok.jobtracker.entity.AtsScoreResult;
import java.time.Instant;
import java.util.List;

public record AtsScoreResponse(
        String id,
        int score,
        List<String> matchedSkills,
        List<String> missingSkills,
        List<String> recommendations,
        String summary,
        String jobTitle,
        String companyName,
        String resumeFileName,
        Instant createdAt) {

    public static AtsScoreResponse from(AtsScoreResult result) {
        return new AtsScoreResponse(
                result.getId(),
                result.getScore(),
                result.getMatchedSkills(),
                result.getMissingSkills(),
                result.getRecommendations(),
                result.getSummary(),
                result.getJobTitle(),
                result.getCompanyName(),
                result.getResumeFileName(),
                result.getCreatedAt());
    }
}
