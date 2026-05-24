package com.ashok.jobtracker.dto.ats;

import com.ashok.jobtracker.entity.AtsScoreResult;
import java.time.Instant;

public record ScoreSummaryResponse(
        String id, int score, String jobTitle, String companyName, String summary, Instant createdAt) {

    public static ScoreSummaryResponse from(AtsScoreResult result) {
        return new ScoreSummaryResponse(
                result.getId(),
                result.getScore(),
                result.getJobTitle(),
                result.getCompanyName(),
                result.getSummary(),
                result.getCreatedAt());
    }
}
