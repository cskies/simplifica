package com.simplifica.controller;

import com.simplifica.dto.SimplifyRequest;
import com.simplifica.dto.SimplifyResponse;
import com.simplifica.entity.Document;
import com.simplifica.entity.User;
import com.simplifica.repo.DocumentRepository;
import com.simplifica.repo.UserRepository;
import com.simplifica.service.AuthService;
import com.simplifica.service.ClaudeService;
import com.simplifica.service.TextExtractorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimplifyController {

    private final ClaudeService claudeService;
    private final TextExtractorService extractorService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AuthService authService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Não autenticado");
        }
        String email = (String) auth.getPrincipal();
        return authService.getUserByEmail(email);
    }

    @PostMapping("/simplify")
    public ResponseEntity<?> simplify(@Valid @RequestBody SimplifyRequest request) {
        User user = getCurrentUser();

        if (!user.canUseDocument()) {
            return ResponseEntity.status(402)
                    .body(Map.of("message", "Cota de documentos atingida. Faça upgrade do plano."));
        }

        try {
            SimplifyResponse response = claudeService.simplify(request.texto());

            // Save to history
            Document doc = Document.builder()
                    .user(user)
                    .originalText(request.texto().substring(0, Math.min(500, request.texto().length())))
                    .resumo(response.resumo())
                    .pontosAtencao(String.join(";", response.pontosAtencao()))
                    .veredicto(response.veredicto())
                    .veredictoMotivo(response.veredictoMotivo())
                    .build();

            documentRepository.save(doc);

            // Increment usage
            user.setDocumentsUsedThisMonth(user.getDocumentsUsedThisMonth() + 1);
            userRepository.save(user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<?> extract(@RequestParam("file") MultipartFile file) {
        User user = getCurrentUser();

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

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Pageable pageable) {
        User user = getCurrentUser();
        Page<Document> docs = documentRepository.findByUserOrderByAnalyzedAtDesc(user, pageable);
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User user = getCurrentUser();
        int remaining = authService.getRemainingDocuments(user);
        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "plan", user.getPlan(),
                "documentsUsed", user.getDocumentsUsedThisMonth(),
                "documentsRemaining", remaining
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
