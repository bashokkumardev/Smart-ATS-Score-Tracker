package com.ashok.jobtracker.support;

import java.nio.file.Files;
import java.nio.file.Path;

public final class GenerateSampleResumePdf {

    private GenerateSampleResumePdf() {}

    public static void main(String[] args) throws Exception {
        Path output = Path.of("test-data", "sample-resume.pdf");
        Files.createDirectories(output.getParent());
        Files.write(output, SampleResumePdf.create());
        System.out.println("Wrote " + output.toAbsolutePath());
    }
}
