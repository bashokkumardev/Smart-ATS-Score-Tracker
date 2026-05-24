package com.ashok.jobtracker.service;

import com.ashok.jobtracker.dto.ats.AtsScoreResponse;
import com.ashok.jobtracker.dto.ats.ScoreSummaryResponse;
import com.ashok.jobtracker.entity.AtsScoreResult;
import com.ashok.jobtracker.exception.BadRequestException;
import com.ashok.jobtracker.exception.ResourceNotFoundException;
import com.ashok.jobtracker.repository.AtsScoreResultRepository;
import com.ashok.jobtracker.security.SecurityUtils;
import com.ashok.jobtracker.service.AtsScoringEngine.AtsScoringResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AtsScoreService {

    private final AtsScoringEngine atsScoringEngine;
    private final ResumeDocumentExtractor resumeDocumentExtractor;
    private final AtsScoreResultRepository atsScoreResultRepository;

    public AtsScoreResponse scoreFromResume(
            MultipartFile resume,
            String jobDescription,
            String jobTitle,
            String companyName) {
        if (!StringUtils.hasText(jobDescription)) {
            throw new BadRequestException("jobDescription is required");
        }
        String resumeText = resumeDocumentExtractor.extractText(resume);
        return calculateAndSave(
                resumeText,
                jobDescription.trim(),
                jobTitle,
                companyName,
                resume.getOriginalFilename());
    }

    public AtsScoreResponse calculateAndSave(
            String resumeText,
            String jobDescription,
            String jobTitle,
            String companyName,
            String resumeFileName) {
        AtsScoringResult scoring;
        try {
            scoring = atsScoringEngine.score(resumeText, jobDescription);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        AtsScoreResult result = AtsScoreResult.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .score(scoring.score())
                .matchedSkills(scoring.matchedSkills())
                .missingSkills(scoring.missingSkills())
                .recommendations(scoring.recommendations())
                .summary(scoring.summary())
                .jobDescription(jobDescription)
                .jobTitle(StringUtils.hasText(jobTitle) ? jobTitle.trim() : null)
                .companyName(StringUtils.hasText(companyName) ? companyName.trim() : null)
                .resumeFileName(resumeFileName)
                .build();

        result = atsScoreResultRepository.save(result);
        return AtsScoreResponse.from(result);
    }

    public List<ScoreSummaryResponse> getHistory() {
        return atsScoreResultRepository.findByUserIdOrderByCreatedAtDesc(SecurityUtils.getCurrentUserId()).stream()
                .map(ScoreSummaryResponse::from)
                .toList();
    }

    public AtsScoreResponse getById(String id) {
        return atsScoreResultRepository
                .findByIdAndUserId(id, SecurityUtils.getCurrentUserId())
                .map(AtsScoreResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Score result not found"));
    }
}
