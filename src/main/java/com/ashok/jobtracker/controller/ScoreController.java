package com.ashok.jobtracker.controller;

import com.ashok.jobtracker.dto.ats.AtsScoreResponse;
import com.ashok.jobtracker.dto.ats.ScoreSummaryResponse;
import com.ashok.jobtracker.service.AtsScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ScoreController {

    private final AtsScoreService atsScoreService;

    @Operation(summary = "Upload resume (PDF/DOCX) and score against job description")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AtsScoreResponse upload(
            @RequestPart("resume") MultipartFile resume,
            @RequestPart("jobDescription") String jobDescription,
            @RequestParam(value = "jobTitle", required = false) String jobTitle,
            @RequestParam(value = "companyName", required = false) String companyName) {
        return atsScoreService.scoreFromResume(resume, jobDescription, jobTitle, companyName);
    }

    @Operation(summary = "List ATS score history for the logged-in user")
    @GetMapping("/history")
    public List<ScoreSummaryResponse> history() {
        return atsScoreService.getHistory();
    }

    @Operation(summary = "Get one ATS score result by id")
    @GetMapping("/{id}")
    public AtsScoreResponse getById(@PathVariable String id) {
        return atsScoreService.getById(id);
    }
}
