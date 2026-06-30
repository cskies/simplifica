package com.simplifica.service;

import com.simplifica.dto.AuthRequest;
import com.simplifica.dto.AuthResponse;
import com.simplifica.entity.User;
import com.simplifica.repo.UserRepository;
import com.simplifica.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email já registrado");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name() != null ? request.name() : request.email())
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getPlan(),
                5, // free tier
                "Registrado com sucesso"
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        int remaining = getRemainingDocuments(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getPlan(),
                remaining,
                "Login realizado com sucesso"
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public int getRemainingDocuments(User user) {
        if (user.getPlan().name().equals("FREE")) {
            return 5 - user.getDocumentsUsedThisMonth();
        }
        if (user.getPlan().name().equals("PRO")) {
            return 100 - user.getDocumentsUsedThisMonth();
        }
        return Integer.MAX_VALUE;
    }
}
