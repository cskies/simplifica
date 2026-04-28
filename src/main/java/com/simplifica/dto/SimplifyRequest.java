package com.simplifica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SimplifyRequest(
    @NotBlank(message = "O texto não pode estar vazio")
    @Size(min = 20, max = 50000, message = "Texto deve ter entre 20 e 50.000 caracteres")
    String texto
) {}
