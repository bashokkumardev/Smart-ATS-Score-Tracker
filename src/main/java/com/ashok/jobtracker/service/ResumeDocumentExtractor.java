package com.ashok.jobtracker.service;

import com.ashok.jobtracker.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ResumeDocumentExtractor {

    private final long maxResumeSizeBytes;

    public ResumeDocumentExtractor(@Value("${app.ats.max-resume-size-bytes:5242880}") long maxResumeSizeBytes) {
        this.maxResumeSizeBytes = maxResumeSizeBytes;
    }

    public String extractText(MultipartFile resume) {
        validateResume(resume);
        String filename = resume.getOriginalFilename() != null ? resume.getOriginalFilename().toLowerCase() : "";

        try {
            if (filename.endsWith(".docx") || isDocxContentType(resume.getContentType())) {
                return extractDocx(resume);
            }
            return extractPdf(resume);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (IOException ex) {
            log.warn("Failed to parse resume file: {}", ex.getMessage());
            throw new BadRequestException("Invalid or corrupted resume file");
        }
    }

    private String extractPdf(MultipartFile resume) throws IOException {
        try (InputStream inputStream = resume.getInputStream();
                PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document).trim();
            if (!StringUtils.hasText(text)) {
                throw new BadRequestException(
                        "Could not extract text from PDF. Upload a text-based PDF, not a scanned image.");
            }
            return text;
        }
    }

    private String extractDocx(MultipartFile resume) throws IOException {
        try (InputStream inputStream = resume.getInputStream();
                XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText().trim();
            if (!StringUtils.hasText(text)) {
                throw new BadRequestException("Could not extract text from DOCX resume");
            }
            return text;
        }
    }

    private void validateResume(MultipartFile resume) {
        if (resume == null || resume.isEmpty()) {
            throw new BadRequestException("Resume file is required");
        }
        if (resume.getSize() > maxResumeSizeBytes) {
            throw new BadRequestException("Resume file exceeds maximum allowed size of 5 MB");
        }

        String filename = resume.getOriginalFilename();
        String contentType = resume.getContentType();
        boolean pdf = filename != null && filename.toLowerCase().endsWith(".pdf")
                || contentType != null && contentType.equalsIgnoreCase("application/pdf");
        boolean docx = filename != null && filename.toLowerCase().endsWith(".docx")
                || isDocxContentType(contentType);
        if (!pdf && !docx) {
            throw new BadRequestException("Only PDF and DOCX resume files are supported");
        }
    }

    private boolean isDocxContentType(String contentType) {
        return contentType != null
                && contentType.equalsIgnoreCase(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
