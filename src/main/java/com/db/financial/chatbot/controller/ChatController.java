package com.db.financial.chatbot.controller;

import com.db.financial.chatbot.model.PromptRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/ask")
public class ChatController {

    private static final String API_KEY = "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";
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
  //  private static final String MODEL_ID = "deepseek/deepseek-r1:free";
  private static final String MODEL_ID = "mistralai/mistral-7b-instruct:free";

    @PostMapping
    public ResponseEntity<String> ask(@RequestBody PromptRequest promptRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_ID);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", promptRequest.getPrompt()));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_URL, entity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
