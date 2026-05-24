package com.ashok.jobtracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ashok.jobtracker.support.SampleResumePdf;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobTrackerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullFlow_registerLoginScoreHistoryAndSecureRegisterAccess() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/auth/register")).andExpect(status().isForbidden());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"name":"Ashok","email":"ashok@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"email":"ashok@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");
        String registerId = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.userId");

        mockMvc.perform(get("/auth/register/" + registerId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ashok@example.com"));

        MockMultipartFile resumePdf = new MockMultipartFile(
                "resume", "resume.pdf", "application/pdf", SampleResumePdf.create());
        MockMultipartFile jobDescription = new MockMultipartFile(
                "jobDescription",
                "",
                "text/plain",
                "Strong Java and Spring Boot required. Kafka and Python are nice to have.".getBytes());

        MvcResult scoreResult = mockMvc.perform(multipart("/score/upload")
                        .file(resumePdf)
                        .file(jobDescription)
                        .param("jobTitle", "Java Developer")
                        .param("companyName", "Acme Corp")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.matchedSkills").isArray())
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.summary").exists())
                .andReturn();

        String scoreId = JsonPath.read(scoreResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/score/history").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(scoreId));

        mockMvc.perform(get("/score/" + scoreId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Java Developer"));
    }
}
