package com.ashok.jobtracker.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AtsScoringEngine {

    private static final List<String> KNOWN_PHRASES = List.of(
            "spring boot",
            "rest api",
            "restful api",
            "node.js",
            "object oriented",
            "design patterns",
            "machine learning",
            "ci/cd",
            "microservices",
            "sql server",
            "power bi",
            "c#",
            "c++",
            ".net");

    private static final Map<String, Integer> SKILL_WEIGHTS = Map.ofEntries(
            Map.entry("java", 10),
            Map.entry("spring boot", 10),
            Map.entry("spring", 8),
            Map.entry("kafka", 8),
            Map.entry("mongodb", 8),
            Map.entry("postgresql", 8),
            Map.entry("mysql", 7),
            Map.entry("sql", 7),
            Map.entry("nosql", 7),
            Map.entry("python", 9),
            Map.entry("c#", 9),
            Map.entry(".net", 9),
            Map.entry("rest api", 8),
            Map.entry("restful api", 8),
            Map.entry("microservices", 8),
            Map.entry("docker", 7),
            Map.entry("kubernetes", 8),
            Map.entry("aws", 8),
            Map.entry("azure", 7),
            Map.entry("git", 6),
            Map.entry("react", 7),
            Map.entry("angular", 7),
            Map.entry("node.js", 7),
            Map.entry("typescript", 7),
            Map.entry("javascript", 7),
            Map.entry("redis", 6),
            Map.entry("elasticsearch", 6),
            Map.entry("graphql", 6),
            Map.entry("hibernate", 6),
            Map.entry("junit", 5),
            Map.entry("maven", 5),
            Map.entry("gradle", 5),
            Map.entry("design patterns", 6),
            Map.entry("object oriented", 5),
            Map.entry("agile", 4),
            Map.entry("scrum", 4),
            Map.entry("linux", 5),
            Map.entry("api", 5),
            Map.entry("security", 5),
            Map.entry("jwt", 5));

    public AtsScoringResult score(String resumeText, String jobDescription) {
        Set<String> resumeSkills = extractSkills(resumeText);
        Set<String> jdSkills = extractSkills(jobDescription);

        if (jdSkills.isEmpty()) {
            throw new IllegalArgumentException("Could not extract skills from job description");
        }
        if (resumeSkills.isEmpty()) {
            throw new IllegalArgumentException("Could not extract skills from resume");
        }

        List<String> matched = jdSkills.stream()
                .filter(resumeSkills::contains)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        List<String> missing = jdSkills.stream()
                .filter(skill -> !resumeSkills.contains(skill))
                .sorted(Comparator.comparingInt(this::weight).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        int totalWeight = jdSkills.stream().mapToInt(this::weight).sum();
        int matchedWeight = matched.stream().mapToInt(this::weight).sum();
        int score = totalWeight == 0 ? 0 : (int) Math.round((matchedWeight * 100.0) / totalWeight);

        List<String> recommendations = missing.stream()
                .limit(5)
                .map(skill -> "Add or highlight '" + displayName(skill) + "' on your resume if you have this experience")
                .collect(Collectors.toCollection(ArrayList::new));

        String summary = buildSummary(score, matched.size(), jdSkills.size());

        return new AtsScoringResult(score, matched, missing, recommendations, summary);
    }

    private String buildSummary(int score, int matchedCount, int totalCount) {
        return matchedCount + " of " + totalCount + " key skills matched (" + score + "% weighted ATS score)";
    }

    Set<String> extractSkills(String text) {
        if (!StringUtils.hasText(text)) {
            return Set.of();
        }
        String normalized = " " + text.toLowerCase(Locale.ROOT).replace('\n', ' ') + " ";
        Set<String> skills = new LinkedHashSet<>();

        for (String phrase : KNOWN_PHRASES) {
            if (containsPhrase(normalized, phrase)) {
                skills.add(phrase);
            }
        }

        for (String skill : SKILL_WEIGHTS.keySet()) {
            if (!skills.contains(skill) && containsPhrase(normalized, skill)) {
                skills.add(skill);
            }
        }

        return skills;
    }

    private boolean containsPhrase(String normalizedText, String phrase) {
        if (phrase.contains("#") || phrase.startsWith(".")) {
            return normalizedText.contains(phrase);
        }
        String regex = "\\b" + Pattern.quote(phrase) + "\\b";
        return Pattern.compile(regex).matcher(normalizedText).find();
    }

    private int weight(String skill) {
        return SKILL_WEIGHTS.getOrDefault(skill, 3);
    }

    private String displayName(String skill) {
        return switch (skill) {
            case "spring boot" -> "Spring Boot";
            case "rest api", "restful api" -> "REST API";
            case "node.js" -> "Node.js";
            case "c#" -> "C#";
            case ".net" -> ".NET";
            default -> skill.substring(0, 1).toUpperCase(Locale.ROOT) + skill.substring(1);
        };
    }

    public record AtsScoringResult(
            int score,
            List<String> matchedSkills,
            List<String> missingSkills,
            List<String> recommendations,
            String summary) {}
}
