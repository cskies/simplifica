package com.simplifica.controller;

import com.simplifica.dto.SimplifyRequest;
import com.simplifica.dto.SimplifyResponse;
import com.simplifica.service.ClaudeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimplifyController {

    private final ClaudeService claudeService;

    public SimplifyController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @PostMapping("/simplify")
    public ResponseEntity<SimplifyResponse> simplify(@Valid @RequestBody SimplifyRequest request) {
        SimplifyResponse response = claudeService.simplify(request.texto());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
