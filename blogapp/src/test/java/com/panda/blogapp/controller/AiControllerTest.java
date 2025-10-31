package com.panda.blogapp.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.blogapp.security.jwt.JwtUtil;

@WebMvcTest(value = AiController.class, properties = {"gemini.api.key=fake-api-key"})
@AutoConfigureMockMvc(addFilters = false)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generateBlog_success() throws Exception {
        // Prepare the request payload
        Map<String, String> requestPayload = Map.of(
                "title", "Test Blog",
                "subTitle", "Test Subtitle"
        );

        // Prepare the Gemini API mock response structure
        Map<String, Object> part = Map.of("text", "<h1>Test Blog</h1><p>Generated content</p>");
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> candidate = Map.of("content", content);
        Map<String, Object> responseBody = Map.of("candidates", List.of(candidate));

        // Mock RestTemplate to return the above response
        ResponseEntity<Map> geminiResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.postForEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Map.class)))
                .thenReturn(geminiResponse);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/api/ai/generate-blog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("<h1>Test Blog</h1><p>Generated content</p>"));
    }

    @Test
    void generateBlog_apiFailure() throws Exception {
        Map<String, String> requestPayload = Map.of(
                "title", "Test Blog",
                "subTitle", "Test Subtitle"
        );

        // Simulate RestTemplate throwing an Exception
        when(restTemplate.postForEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Map.class)))
                .thenThrow(new RuntimeException("API down"));

        mockMvc.perform(post("/api/ai/generate-blog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("AI generation failed: API down"));
    }
}
