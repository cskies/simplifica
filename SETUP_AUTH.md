# Simplifica — Setup com Auth + Database

## 📋 Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker + Docker Compose (para PostgreSQL)
- Chave de API da Anthropic

## 🚀 Quick Start (Local)

### 1. Start PostgreSQL
```bash
docker-compose up -d
```

### 2. Configure Environment
```bash
cp .env.example .env
# Edit .env and add your ANTHROPIC_API_KEY
```

### 3. Build & Run
```bash
mvn clean package -DskipTests
java -jar target/simplifica-1.0.0.jar
```

Server rodará em `http://localhost:8080`

---

## 🔐 Autenticação

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "name": "John Doe",
  "plan": "FREE",
  "documentsRemaining": 5,
  "message": "Registrado com sucesso"
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Use Token
```bash
curl -X POST http://localhost:8080/api/simplify \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "texto": "Seu contrato aqui..."
  }'
```

---

## 📊 API Endpoints

### Auth
- `POST /api/auth/register` — Novo usuário
- `POST /api/auth/login` — Login existente

### Documents
- `POST /api/simplify` — Analisar documento (requer token)
- `POST /api/extract` — Extrair texto de PDF/DOCX (requer token)
- `GET /api/history?page=0&size=10` — Histórico de análises (requer token)
- `GET /api/profile` — Dados do usuário (requer token)

### Utility
- `GET /api/health` — Health check (sem autenticação)

---

## 🎯 Quotas

| Plano | Limite Mensal |
|-------|-------------|
| FREE | 5 documentos/mês |
| PRO | 100 documentos/mês |
| BUSINESS | Ilimitado |

Quotas resetam automaticamente todo 1º do mês às 00:00 UTC.

---

## 🗄️ Database Schema

Tabelas criadas automaticamente via Hibernate:

- `users` — Contas de usuário
  - `id`, `email` (unique), `password`, `name`, `plan`, `documents_used_this_month`, `created_at`, `plan_renews_at`

- `documents` — Histórico de análises
  - `id`, `user_id` (FK), `original_text`, `file_name`, `resumo`, `pontos_atencao`, `veredicto`, `veredicto_motivo`, `analyzed_at`, `title`

---

## 🐛 Troubleshooting

### Erro: "Cannot connect to database"
```bash
# Verificar se PostgreSQL está rodando
docker ps

# Reiniciar se necessário
docker-compose restart postgres
```

### Erro: "Invalid JWT token"
- Verificar se o token foi transmitido no header `Authorization: Bearer <token>`
- Token válido por 24 horas (configurável em `jwt.expiration`)

### Erro: "Quota atingida"
- Apenas Free users são limitados a 5 docs/mês
- Fazer upgrade para PRO ($9.99/mês) ou BUSINESS ($49/mês)

---

## 🚢 Deploy no Railway

1. Conectar repository ao Railway
2. Railway automaticamente detecta `Dockerfile`
3. Definir variáveis de ambiente:
   ```
   ANTHROPIC_API_KEY=...
   JWT_SECRET=seu-secret-aqui (min 32 chars)
   DATABASE_URL=postgres://... (Railway provisiona automaticamente)
   ```

4. Deploy automático em cada push para `main`

---

## 📝 Notes

- JWT secret pode ser gerado com: `openssl rand -base64 32`
- Em produção, usar HTTPS + CORS configurado
- Implementar Stripe para pagamentos (próxima fase)
