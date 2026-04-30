package com.simplifica.controller;

import com.simplifica.dto.SimplifyRequest;
import com.simplifica.dto.SimplifyResponse;
import com.simplifica.service.ClaudeService;
import com.simplifica.service.TextExtractorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimplifyController {

    private final ClaudeService claudeService;
    private final TextExtractorService extractorService;

    public SimplifyController(ClaudeService claudeService, TextExtractorService extractorService) {
        this.claudeService = claudeService;
        this.extractorService = extractorService;
    }

    @PostMapping("/simplify")
    public ResponseEntity<SimplifyResponse> simplify(@Valid @RequestBody SimplifyRequest request) {
        SimplifyResponse response = claudeService.simplify(request.texto());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extract(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Arquivo vazio."));
        }
        try {
            String texto = extractorService.extract(file);
            if (texto.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Não foi possível extrair texto do arquivo."));
            }
            return ResponseEntity.ok(Map.of("texto", texto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Erro ao processar o arquivo."));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
