package com.simplifica.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.simplifica.dto.SimplificaResult;
import com.simplifica.dto.SimplifyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaudeService {

    // Prompt cacheado com TTL de 1h — pago 1× no primeiro request, 0.1× nos seguintes
    private static final String SYSTEM_PROMPT = """
            Você é um especialista em traduzir documentos jurídicos e bancários para linguagem \
            simples e acessível para cidadãos comuns.

            Dado o documento fornecido:
            1. Explique em linguagem simples o que significa
            2. Liste os pontos de atenção (riscos, obrigações, prazos, cláusulas importantes)
            3. Dê um veredicto: "seguro_assinar", "atencao_necessaria" ou "cuidado_alto"

            Critérios para o veredicto:
            - "seguro_assinar": documento padrão, sem cláusulas abusivas, termos justos
            - "atencao_necessaria": tem pontos que merecem negociação ou esclarecimento antes de assinar
            - "cuidado_alto": contém cláusulas abusivas, riscos significativos ou condições muito desfavoráveis

            Seja direto, use vocabulário do dia a dia. Evite jargão jurídico.
            """;

    private final AnthropicClient client;

    public ClaudeService(AnthropicClient client) {
        this.client = client;
    }

    public SimplifyResponse simplify(String texto) {
        // Structured output: o SDK gera o JSON Schema a partir de SimplificaResult
        // e o Claude retorna JSON tipado — sem parsing manual
        StructuredMessageCreateParams<SimplificaResult> params = MessageCreateParams.builder()
                .model("claude-sonnet-4-6")
                .maxTokens(2048L)
                .systemOfTextBlockParams(List.of(
                        TextBlockParam.builder()
                                .text(SYSTEM_PROMPT)
                                .cacheControl(CacheControlEphemeral.builder().build())
                                .build()))
                .outputConfig(SimplificaResult.class)
                .addUserMessage("Analise este documento:\n\n" + texto)
                .build();

        SimplificaResult result = client.messages().create(params)
                .content()
                .stream()
                .flatMap(cb -> cb.text().stream())
                .map(typed -> typed.text())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Resposta vazia do Claude"));

        return new SimplifyResponse(
                result.resumo(),
                result.pontosAtencao(),
                result.veredicto(),
                result.veredictoMotivo()
        );
    }
}
