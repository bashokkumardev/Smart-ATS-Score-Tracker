package com.ashok.jobtracker.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public final class SampleResumePdf {

    private SampleResumePdf() {}

    public static byte[] create() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(font, 12);
                content.newLineAtOffset(50, 700);
                content.showText("Ashok Kumar - Java Developer");
                content.newLineAtOffset(0, -20);
                content.showText("Skills: Java, Spring Boot, MongoDB, REST API, PostgreSQL");
                content.newLineAtOffset(0, -20);
                content.showText("Experience building microservices and REST backends.");
                content.endText();
            }

            document.save(output);
            return output.toByteArray();
        }
    }
}
