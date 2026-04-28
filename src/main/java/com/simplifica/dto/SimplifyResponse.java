package com.simplifica.dto;

import java.util.List;

public record SimplifyResponse(
    String resumo,
    List<String> pontosAtencao,
    String veredicto,
    String veredictoMotivo
) {}
