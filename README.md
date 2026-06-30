# Simplifica — AI-Powered Document Analyzer

**Traduz documentos jurídicos e bancários para linguagem simples com análise de risco.**

O usuário:
1. Faz login ou se registra
2. Cola ou faz upload de um contrato, termo bancário ou documento governamental
3. Recebe instantaneamente:
   - **Resumo** em linguagem acessível
   - **Pontos de atenção** (riscos, obrigações, prazos)
   - **Veredicto de segurança**: 🟢 Seguro | 🟡 Atenção | 🔴 Cuidado

## 🎯 Proposta de Valor

- **Para pessoas físicas:** Entender contratos/documentos antes de assinar
- **Para advogados:** Análise rápida de documentos, foco em pontos críticos
- **Para contadores:** Automatizar análise de termos bancários/comerciais
- **Para startups:** Integrar análise via API

---

## 🔧 Tech Stack

| Camada    | Tecnologia                          |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3.2.5 · Java 21         |
| Database  | PostgreSQL 16 (com Spring Data JPA) |
| Auth      | JWT (Spring Security)               |
| IA        | Claude API (Sonnet 4.6)             |
| Frontend  | HTML5 + CSS3 + Vanilla JS (sem deps)|
| Deploy    | Railway (backend) + Vercel (frontend)|
| Dev Stack | Docker Compose, Maven, Git          |

---

## 📋 Features

### **Core**
- ✅ Análise de documentos com Claude IA
- ✅ Extração de texto de PDF e DOCX
- ✅ Veredicto de segurança em 3 níveis
- ✅ Resposta estruturada (resumo + pontos + veredicto)

### **Authentication & Billing**
- ✅ JWT-based register/login
- ✅ Subscription plans (FREE/PRO/BUSINESS)
- ✅ Monthly quota enforcement (5/100/unlimited)
- ✅ Document history per user
- ✅ Auto quota reset (mensal)

### **API**
- ✅ REST endpoints com token authentication
- ✅ Rate limiting preparado
- ✅ CORS configurável
- ✅ Health check endpoint

### **Infrastructure**
- ✅ Docker container (multi-stage build)
- ✅ Docker Compose para dev (PostgreSQL)
- ✅ Railway deployment ready
- ✅ Environment-based config (.env)

---

## Pré-requisitos

- Java 21+
- Maven 3.9+ — ou use o script `run.sh` que aponta para o Maven do sdkman
- Chave da API Anthropic → [console.anthropic.com/settings/keys](https://console.anthropic.com/settings/keys)

---

## 🚀 Quick Start

### **Pré-requisitos**
- Java 21+
- Maven 3.9+ (ou use `./run.sh`)
- Docker + Docker Compose (para PostgreSQL)
- API key Anthropic → [console.anthropic.com/settings/keys](https://console.anthropic.com/settings/keys)

---

### **1. Clone o projeto**
```bash
git clone <url-do-repo>
cd simplifica
```

---

### **2. Inicie PostgreSQL**
```bash
docker-compose up -d
```

Verifica que está rodando:
```bash
docker ps | grep simplifica-db
```

---

### **3. Configure as variáveis de ambiente**
```bash
cp .env.example .env
# Edite .env com sua ANTHROPIC_API_KEY
```

Conteúdo do `.env`:
```env
ANTHROPIC_API_KEY=sk-ant-...seu-token-aqui...
DATABASE_URL=jdbc:postgresql://localhost:5432/simplifica
DB_USER=simplifica
DB_PASSWORD=simplifica123
JWT_SECRET=sua-chave-secreta-minimo-32-caracteres
PORT=8080
```

---

### **4. Build & Run**

**Opção A — Maven direto:**
```bash
mvn clean package -DskipTests
java -jar target/simplifica-*.jar
```

**Opção B — Maven + Spring Boot:**
```bash
mvn spring-boot:run
```

**Opção C — Script (recomendado):**
```bash
./run.sh
```

**Opção D — IntelliJ IDEA:**
1. `Run → Edit Configurations → + → Spring Boot`
2. Main class: `com.simplifica.SimplificaApplication`
3. Environment vars: `ANTHROPIC_API_KEY=...`
4. Click Run ▶

---

### **5. Acesse a aplicação**

**Frontend:** http://localhost:8080

**API Base:** http://localhost:8080/api

---

### **6. Teste a API**

Registre-se:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.com",
    "password": "senha123456",
    "name": "Test User"
  }'
```

Copie o token retornado e use:
```bash
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8080/api/simplify \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "texto": "Contrato de aluguel..."
  }'
```

---

### **Troubleshooting**

| Erro | Solução |
|------|---------|
| `Connection refused (5432)` | `docker-compose up -d` |
| `ANTHROPIC_API_KEY not found` | Adicionar a `.env` |
| `Unauthorized: Invalid JWT` | Token expirou (24h), login novamente |
| `Quota exceeded` | FREE tier = 5 docs/mês, upgrade para PRO |
| `Port 8080 already in use` | `PORT=8081 mvn spring-boot:run` |

---

## 📁 Estrutura do Projeto

```
simplifica/
├── src/main/
│   ├── java/com/simplifica/
│   │   ├── config/
│   │   │   ├── AnthropicConfig.java      # Bean Anthropic SDK
│   │   │   ├── CorsConfig.java           # CORS setup
│   │   │   └── SecurityConfig.java       # JWT + Spring Security
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java       # POST /api/auth/register, login
│   │   │   └── SimplifyController.java   # Document analysis + auth required
│   │   │
│   │   ├── service/
│   │   │   ├── ClaudeService.java        # IA integration logic
│   │   │   ├── AuthService.java          # Auth + quota management
│   │   │   └── TextExtractorService.java # PDF/DOCX extraction
│   │   │
│   │   ├── entity/
│   │   │   ├── User.java                 # User model (auth + quotas)
│   │   │   ├── Document.java             # Analysis history per user
│   │   │   └── SubscriptionPlan.java     # ENUM: FREE, PRO, BUSINESS
│   │   │
│   │   ├── dto/
│   │   │   ├── SimplifyRequest.java      # { "texto": "..." }
│   │   │   ├── SimplifyResponse.java     # { "resumo", "pontosAtencao", ... }
│   │   │   ├── SimplificaResult.java     # Claude schema (internal)
│   │   │   ├── AuthRequest.java          # { "email", "password", "name" }
│   │   │   └── AuthResponse.java         # { "token", "plan", "remaining" }
│   │   │
│   │   ├── repo/
│   │   │   ├── UserRepository.java       # JPA: findByEmail()
│   │   │   └── DocumentRepository.java   # JPA: findByUserOrderByDate()
│   │   │
│   │   ├── security/
│   │   │   ├── JwtUtil.java              # Token generation/validation
│   │   │   └── JwtFilter.java            # OncePerRequestFilter for JWT
│   │   │
│   │   ├── scheduler/
│   │   │   └── QuotaResetScheduler.java  # Cron: reset quotas monthly
│   │   │
│   │   └── SimplificaApplication.java    # Spring Boot entry point
│   │
│   └── resources/
│       ├── application.properties        # DB + JWT + API config
│       └── static/index.html             # Frontend SPA
│
├── docker-compose.yml                    # PostgreSQL local dev
├── Dockerfile                            # Multi-stage Java build
├── railway.toml                          # Railway deployment config
├── .env.example                          # Env vars template
├── SETUP_AUTH.md                         # Auth setup guide
├── README.md                             # This file
├── run.sh                                # Dev quick-start
└── pom.xml                               # Maven dependencies
```

### **Layers Explanation**

| Layer | Responsabilidade | Files |
|-------|-----------------|-------|
| **Controller** | HTTP endpoints, request validation | AuthController, SimplifyController |
| **Service** | Business logic, IA integration, auth | ClaudeService, AuthService |
| **Entity** | Domain models, DB schema | User, Document, SubscriptionPlan |
| **Repo** | Database access (JPA) | UserRepository, DocumentRepository |
| **Security** | JWT generation, token validation | JwtUtil, JwtFilter |
| **Scheduler** | Async tasks (quota reset) | QuotaResetScheduler |
| **Config** | Spring beans, security config | SecurityConfig, AnthropicConfig |
| **DTO** | Request/response contracts | AuthRequest, SimplifyRequest, etc |

---

## 🔌 API Endpoints

### **Authentication**

#### `POST /api/auth/register`
Criar nova conta.

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "senha123456",
    "name": "John Doe"
  }'
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...",
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "FREE",
  "documentsRemaining": 5,
  "message": "Registrado com sucesso"
}
```

#### `POST /api/auth/login`
Login com email/password existente.

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "senha123456"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...",
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "FREE",
  "documentsRemaining": 5,
  "message": "Login realizado com sucesso"
}
```

---

### **Document Analysis** (Requires Authentication)

All endpoints below require `Authorization: Bearer <token>` header.

#### `POST /api/simplify`
Analisa um documento de texto.

**Request:**
```bash
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8080/api/simplify \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "texto": "O presente instrumento tem por objeto a locação de imóvel..."
  }'
```

**Response (200 OK):**
```json
{
  "resumo": "Este contrato significa que o locatário aluga o imóvel pelo período de 12 meses...",
  "pontosAtencao": [
    "Multa de 10% sobre o valor total em caso de rescisão antecipada",
    "Reajuste anual pelo IGPM sem limite de teto",
    "Depósito caução não reajustável"
  ],
  "veredicto": "atencao_necessaria",
  "veredictoMotivo": "O contrato possui cláusulas de reajuste e multa que merecem negociação antes da assinatura."
}
```

**HTTP Status Codes:**
- `200 OK` — Análise bem-sucedida
- `402 Payment Required` — Quota atingida (fazer upgrade)
- `401 Unauthorized` — Token inválido/expirado
- `400 Bad Request` — Erro de validação

---

#### `POST /api/extract`
Extrai texto de PDF ou DOCX.

**Request:**
```bash
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8080/api/extract \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@seu_documento.pdf"
```

**Response (200 OK):**
```json
{
  "texto": "O texto extraído do PDF aparece aqui..."
}
```

---

#### `GET /api/history?page=0&size=10`
Busca histórico de análises do usuário (paginado).

**Request:**
```bash
TOKEN="seu_token_aqui"

curl -X GET "http://localhost:8080/api/history?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "fileName": "contrato_aluguel.pdf",
      "veredicto": "atencao_necessaria",
      "analyzedAt": "2024-06-28T14:30:00",
      "title": "Contrato Aluguel Apto 42"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "currentPage": 0
}
```

---

#### `GET /api/profile`
Retorna dados do usuário autenticado.

**Request:**
```bash
TOKEN="seu_token_aqui"

curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer $TOKEN"
```

**Response (200 OK):**
```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "PRO",
  "documentsUsed": 47,
  "documentsRemaining": 53
}
```

---

### **Utility**

#### `GET /api/health`
Health check (sem autenticação).

**Request:**
```bash
curl http://localhost:8080/api/health
```

**Response (200 OK):**
```json
{ "status": "ok" }
```

---

## 📊 Veredictos & Risk Levels

| Valor | Ícone | Significado |
|-------|-------|-------------|
| `seguro_assinar` | 🟢 | Documento padrão, termos justos, sem problemas |
| `atencao_necessaria` | 🟡 | Pontos que merecem esclarecimento ou negociação antes de assinar |
| `cuidado_alto` | 🔴 | Cláusulas abusivas, riscos significativos, revisar com especialista |

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

## 🏗️ Arquitetura

### **Clean Architecture (Layered)**

O projeto segue **Clean Architecture com separação clara de responsabilidades**:

```
┌─────────────────────────────────────────────────────┐
│  Frontend (HTML + JS)                               │
│  - Chamadas AJAX para API                           │
│  - UI com tabs (texto vs upload)                    │
│  - Renderiza veredicto com ícone + cores           │
└──────────────────┬──────────────────────────────────┘
                   │ HTTP + JWT Token
┌──────────────────▼──────────────────────────────────┐
│  REST Controllers (AuthController, SimplifyController)
│  - Validação de request (@Valid)                   │
│  - Autenticação via JWT                            │
│  - Quota check                                      │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│  Services (Business Logic)                          │
│  - AuthService: register, login, quota mgmt        │
│  - ClaudeService: IA integration, prompt building  │
│  - TextExtractorService: PDF/DOCX parsing          │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│  Repositories (Data Access)                         │
│  - UserRepository: JPA queries                      │
│  - DocumentRepository: history + pagination        │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│  Database (PostgreSQL)                              │
│  - users (auth + quotas)                           │
│  - documents (analysis history)                     │
└─────────────────────────────────────────────────────┘
```

### **Key Architectural Decisions**

1. **Stateless Microservice**
   - Cada request é independente
   - Session não é armazenada no servidor
   - JWT permite scale horizontal

2. **Structured Output (Claude)**
   - Usa JSON Schema do SDK
   - Garante resposta tipada
   - Sem parsing manual

3. **Prompt Caching**
   - System prompt cached por 1 hora
   - Primeiros requests pagam full, resto ~10%
   - Reduz custos significativamente

4. **JWT Authentication**
   - Token gerado no register/login
   - JwtFilter valida em cada request
   - Expira em 24 horas (configurável)

5. **Monthly Quota Reset**
   - Scheduler roda 1º do mês às 00:00 UTC
   - Reseta `documentsUsedThisMonth` para 0
   - Suporta múltiplos planos (FREE/PRO/BUSINESS)

6. **Document Persistence**
   - Cada análise salva no DB com user + timestamp
   - Permite histórico completo
   - Integrado com quota system

---

## 🤖 Como Funciona a IA

### **Request → Claude → Response**

```
1. User submits document text via /api/simplify
   ↓
2. AuthService valida token + quota
   ↓
3. ClaudeService prepara request com:
   - System prompt (cached)
   - User's document text
   - Output schema (SimplificaResult.class)
   ↓
4. Claude API retorna estruturado:
   {
     "resumo": "...",
     "pontosAtencao": [...],
     "veredicto": "...",
     "veredictoMotivo": "..."
   }
   ↓
5. Document saved to DB (user + analysis + timestamp)
   ↓
6. User quota incremented
   ↓
7. SimplifyResponse returned to frontend
```

### **Otimizações**

| Técnica | Impacto | Como Funciona |
|---------|---------|---------------|
| **Prompt Caching** | 90% redução (custo) | System prompt cached 1h, reutilizado |
| **Structured Output** | Sem parsing | Claude retorna JSON tipado |
| **Model Choice** | 6x mais barato | Sonnet 4.6 vs Opus 4.7 |
| **No Streaming** | Simples + previsível | Full response de uma vez |
