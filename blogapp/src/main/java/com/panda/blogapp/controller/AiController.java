package com.panda.blogapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
	
	private final RestTemplate restTemplate; 

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @PostMapping("/generate-blog")
    public ResponseEntity<Map<String, String>> generateBlog(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "Untitled Blog");
        String subTitle = payload.getOrDefault("subTitle", "");

        // Build the prompt for Gemini
        String prompt = String.format("""
                Write a blog post titled "%s" with subtitle "%s".

                Requirements:
                - The output MUST be valid HTML with <h1>, <h2>, and <p> tags.
                - DO NOT include <html>, <head>, <body>, or <title> tags.
                - DO NOT use markdown or plain text.
                - Keep formatting clean and structured for direct insertion into a rich text editor.
                - Include:
                  1. An engaging introduction
                  2. 2–3 well-structured sections with <h2> headings
                  3. A short conclusion
                """, title, subTitle);

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

            // Build request body as per Gemini API structure
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> body = Map.of("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

//            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> geminiResponse = restTemplate.postForEntity(endpoint, entity, Map.class);

            // Extract the generated text from the Gemini response
            Map<String, Object> responseBody = geminiResponse.getBody();
            StringBuilder generatedContent = new StringBuilder();

            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<?> candidates = (List<?>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> contentMap = (Map<?, ?>) firstCandidate.get("content");
                    List<?> parts = (List<?>) contentMap.get("parts");

                    for (Object partObj : parts) {
                        Map<?, ?> partMap = (Map<?, ?>) partObj;
                        if (partMap.get("text") != null) {
                            generatedContent.append(partMap.get("text"));
                        }
                    }
                }
            }

            Map<String, String> result = new HashMap<>();
            result.put("content", generatedContent.toString());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "AI generation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
