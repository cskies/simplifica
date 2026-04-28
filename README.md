# Simplifica

Tradutor de documentos jurídicos e bancários para linguagem simples, powered by Claude AI.

O usuário cola um contrato, termo bancário ou documento governamental e recebe:
- **Resumo** em linguagem do dia a dia
- **Pontos de atenção** (riscos, obrigações, prazos)
- **Veredicto**: `Seguro para assinar` / `Atenção necessária` / `Cuidado alto`

## Stack

| Camada    | Tecnologia                          |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3.2.5 · Java 21         |
| IA        | Claude API (`claude-opus-4-7`)      |
| Frontend  | HTML + CSS + JS puro (sem deps)     |
| Deploy    | Railway (backend) · Vercel (frontend)|

---

## Pré-requisitos

- Java 21+
- Maven 3.9+ — ou use o script `run.sh` que aponta para o Maven do sdkman
- Chave da API Anthropic → [console.anthropic.com/settings/keys](https://console.anthropic.com/settings/keys)

---

## Rodando localmente

### 1. Clone e entre no projeto

```bash
git clone <url-do-repo>
cd simplifica
```

### 2. Configure a chave da API

Crie o arquivo `.env` na raiz do projeto:

```bash
echo 'ANTHROPIC_API_KEY=sk-ant-api03-...' > .env
```

> O `.env` já está no `.gitignore` — nunca será commitado.

### 3. Execute

**Opção A — script pronto (recomendado):**

```bash
./run.sh
```

**Opção B — exportando a variável manualmente:**

```bash
export ANTHROPIC_API_KEY=sk-ant-api03-...
~/.sdkman/candidates/maven/current/bin/mvn spring-boot:run
```

**Opção C — IntelliJ IDEA:**

1. `Run → Edit Configurations → + → Spring Boot`
2. Main class: `com.simplifica.SimplificaApplication`
3. Em **Environment variables**: `ANTHROPIC_API_KEY=sk-ant-api03-...`
4. Clique em Run ▶

### 4. Acesse

```
http://localhost:8080
```

---

## Estrutura do projeto

```
simplifica/
├── src/main/
│   ├── java/com/simplifica/
│   │   ├── config/
│   │   │   ├── AnthropicConfig.java   # Bean do cliente Anthropic
│   │   │   └── CorsConfig.java        # CORS configurável
│   │   ├── controller/
│   │   │   └── SimplifyController.java # POST /api/simplify
│   │   ├── dto/
│   │   │   ├── SimplifyRequest.java   # Entrada validada
│   │   │   ├── SimplifyResponse.java  # Resposta ao frontend
│   │   │   └── SimplificaResult.java  # Schema tipado para o Claude
│   │   └── service/
│   │       └── ClaudeService.java     # Lógica de chamada à API
│   └── resources/
│       ├── application.properties
│       └── static/index.html          # Frontend completo
├── .env                               # Sua chave (gitignored)
├── .gitignore
├── run.sh                             # Atalho para rodar
└── pom.xml
```

---

## API

### `POST /api/simplify`

**Request:**
```json
{ "texto": "O presente instrumento tem por objeto..." }
```

**Response:**
```json
{
  "resumo": "Este contrato significa que...",
  "pontosAtencao": [
    "Multa de 10% sobre o valor total em caso de rescisão",
    "Reajuste anual pelo IGPM sem limite de teto"
  ],
  "veredicto": "atencao_necessaria",
  "veredictoMotivo": "O contrato possui cláusulas de reajuste e multa que merecem negociação antes da assinatura."
}
```

**Veredictos possíveis:**

| Valor                | Significado                                     |
|----------------------|-------------------------------------------------|
| `seguro_assinar`     | Documento padrão, termos justos                 |
| `atencao_necessaria` | Pontos que merecem esclarecimento ou negociação |
| `cuidado_alto`       | Cláusulas abusivas ou riscos significativos     |

### `GET /api/health`

```json
{ "status": "ok" }
```

---

## Deploy

### Backend → Railway

1. Push do código para o GitHub
2. No Railway: **New Project → Deploy from GitHub**
3. Adicionar variável de ambiente: `ANTHROPIC_API_KEY=sk-ant-...`
4. O `railway.toml` já configura o build automaticamente

### Frontend → Vercel / Netlify

O `index.html` é auto-contido e pode ser hospedado separadamente.

Quando frontend e backend estiverem em domínios diferentes, adicione antes do `</head>` do `index.html`:

```html
<script>window.API_BASE_URL = 'https://seu-backend.railway.app';</script>
```

Em seguida arraste o `index.html` para o Vercel/Netlify — publicado em 2 minutos.

---

## Configurações

| Propriedade             | Padrão | Descrição                                      |
|-------------------------|--------|------------------------------------------------|
| `ANTHROPIC_API_KEY`     | —      | Obrigatória. Chave da API Anthropic            |
| `PORT`                  | `8080` | Porta do servidor (Railway injeta automaticamente) |
| `cors.allowed-origins`  | `*`    | Em produção, trocar pelo domínio do frontend   |

Para trocar o modelo (ex.: usar Sonnet 4.6 que é ~6× mais barato), edite `ClaudeService.java`:

```java
.model("claude-sonnet-4-6")  // era "claude-opus-4-7"
```

---

## Como funciona a IA

1. O system prompt é enviado com **cache de 1 hora** — o primeiro request escreve o cache, os seguintes pagam ~10% do custo de input
2. A resposta do Claude é **structured output tipado** — o SDK gera o JSON Schema automaticamente a partir do record `SimplificaResult`, sem parsing manual
3. O frontend usa a resposta diretamente, sem processamento adicional
