package com.simplifica.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record SimplificaResult(
    @JsonPropertyDescription("Explicação clara e simples do que o documento significa, em 3 a 5 parágrafos curtos")
    String resumo,

    @JsonPropertyDescription("Lista dos pontos de atenção: riscos, obrigações, prazos e cláusulas importantes")
    List<String> pontosAtencao,

    @JsonPropertyDescription("Veredicto geral: exatamente um dos valores 'seguro_assinar', 'atencao_necessaria' ou 'cuidado_alto'")
    String veredicto,

    @JsonPropertyDescription("Justificativa do veredicto em 1 a 2 frases")
    String veredictoMotivo
) {}
