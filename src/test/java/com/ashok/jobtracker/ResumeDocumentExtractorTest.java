package com.ashok.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ashok.jobtracker.exception.BadRequestException;
import com.ashok.jobtracker.service.ResumeDocumentExtractor;
import com.ashok.jobtracker.support.SampleResumePdf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ResumeDocumentExtractorTest {

    @Autowired
    private ResumeDocumentExtractor resumeDocumentExtractor;

    @Test
    void extractText_readsSkillsFromPdf() throws Exception {
        MockMultipartFile resume = new MockMultipartFile(
                "resume", "resume.pdf", "application/pdf", SampleResumePdf.create());

        String text = resumeDocumentExtractor.extractText(resume);

        assertThat(text).containsIgnoringCase("Java");
        assertThat(text).containsIgnoringCase("Spring Boot");
    }

    @Test
    void extractText_rejectsUnsupportedFormat() {
        MockMultipartFile resume =
                new MockMultipartFile("resume", "resume.txt", "text/plain", "Java Spring".getBytes());

        assertThatThrownBy(() -> resumeDocumentExtractor.extractText(resume))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("PDF and DOCX");
    }
}
