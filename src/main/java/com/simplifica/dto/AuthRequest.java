package com.simplifica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    @Email(message = "Email inválido")
    String email,

    @NotBlank
    @Size(min = 8, message = "Senha deve ter mínimo 8 caracteres")
    String password,

    String name // opcional, para register
) {}
