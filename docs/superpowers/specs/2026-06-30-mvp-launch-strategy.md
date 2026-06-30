# Simplifica — MVP Launch Strategy (1 Week)

**Date:** 2026-06-30  
**Goal:** Deploy MVP to production + acquire first paying users via Instagram  
**Timeline:** 7 days  
**Target Audience:** Pessoas físicas (individuals)  
**Primary Channel:** Instagram (@simplifica)

---

## Strategy Overview

**Approach:** Fast Launch + Content-Driven Growth

Launch a working MVP to Railway with manual payment processing. Growth lever is viral Instagram content focused on contract analysis (specifically loan/credit contracts) that drives traffic to the app. Monetize through manual email-based upgrades.

---

## 1. Technical Stack & Deployment

### Backend (Already Built)
- Spring Boot 3.2.5 + Java 17
- PostgreSQL 16
- Spring Security + JWT
- Claude API integration

### Frontend (Already Built)
- HTML5 + CSS3 + Vanilla JS
- Embedded in Spring Boot JAR

### Deployment Target
- **Railway** (railway.app) — deploy backend + Postgres
- **Domain:** simplifica.app (or similar)
- **SSL:** Railway auto-provisions

### Changes Required for MVP
1. Add `trial_starts_at` column to `users` table (Hibernate auto-creates via DDL)
2. Add trial check in `/api/simplify` endpoint — return 403 if trial expired + not PRO
3. Create "Upgrade PRO" form in frontend (email form → POST to `/api/upgrade-request`)
4. Add `/api/upgrade-request` endpoint (stores request in DB, sends to admin email)

---

## 2. Trial System Implementation

### Database Schema Addition
```sql
ALTER TABLE users ADD COLUMN trial_starts_at TIMESTAMP;
ALTER TABLE users ADD COLUMN trial_activated BOOLEAN DEFAULT FALSE;
```
Hibernate will handle this automatically via `ddl-auto=update`.

### Logic
- User registers → `trial_starts_at = now()`, `trial_activated = true`
- Frontend checks: if `trial_active` and `documents_used < 5` → show warning at day 6+
- `/api/simplify` endpoint: rejects request if trial expired AND user plan is FREE
- Response includes: `{ "trial_active": boolean, "days_remaining": int }`

### Frontend Behavior
- Day 1-5: Normal usage
- Day 6-7: Show banner "Trial expira em X dias — clique para upgrade"
- Day 8+: Block `/api/simplify` calls with "Trial expirado — upgrade para PRO"

---

## 3. Manual Monetization Flow

### Upgrade Request Endpoint
```
POST /api/upgrade-request
Content-Type: application/json
Authorization: Bearer <token>

{
  "plan": "PRO",  // or "BUSINESS"
  "message": "Quero upgrade"
}

Response: { "status": "request_received" }
```

Stores in DB:
- `upgrade_requests` table (user_id, plan, created_at, status)
- Sends email to admin@simplifica with request details

### Admin Manual Process
1. Receives email with user details
2. Verifies user in Railway Postgres dashboard
3. Updates `users.plan = 'PRO'` + sets `plan_renews_at`
4. Sends user: "Pronto! Seu acesso PRO ativado. Validade até [date]"
5. Or: sends Stripe invoice link + marks as "pending_payment"

### Payment Options (Manual)
- Pix (most common in Brazil)
- Invoice link (Stripe, but manual — don't automate checkout)
- Bank transfer

---

## 4. Instagram Strategy

### Account Setup
- Create new account: @simplifica (or @simplifica.app)
- Bio: "Entenda contratos antes de assinar | Análise com IA"
- Link in bio → simplifica.app
- Profile: Professional account (for analytics)

### Content Strategy
**Hook:** Contract analysis that shows risks people don't notice

**Content Pillars (3-5 posts, Days 1-2):**
1. "Analisei contrato de empréstimo do Banco X — 3 cláusulas absurdas"
   - Video or carousel showing: Original text → Risk highlight → Simple explanation
2. "Termos de crédito que ninguém lê (e custam caro)"
   - Multiple examples from different lenders
3. "Como a IA ajuda a entender contratos complexos"
   - Teaser of Simplifica functionality

**Format:** Short videos (15-30s reels) or carousels (5-8 slides)

**Call-to-Action:** Consistent: "Link na bio — análise grátis"

### Launch Timeline
- **Day 1-2:** Create 5 posts (can be pre-recorded/pre-written)
- **Day 3:** Post main launch content
- **Day 4-5:** Daily stories, engage comments, share in relevant communities (Reddit r/financas, Discord servers, FB groups)
- **Day 6-7:** Monitor analytics, respond to DMs, optimize top-performing posts

### Viral Elements
- Shock value: "Você não vai acreditar nessa cláusula"
- Relatable: Something 50%+ of audience has signed
- Actionable: "Use o link na bio para analisar seu contrato"

---

## 5. User Journey

```
[Instagram Post]
       ↓
[Click link in bio]
       ↓
[simplifica.app loads]
       ↓
[User sees: "Entenda contratos com IA"]
       ↓
[Sign up (email/password)]
       ↓
[Trial activated (7 days)]
       ↓
[Try analysis feature]
       ↓
[Day 6-7: "Trial expira em X dias"]
       ↓
[Click "Upgrade PRO"]
       ↓
[Email form: Name + Email + Plan]
       ↓
[You receive email with request]
       ↓
[Manual upgrade in DB]
       ↓
[User gets access to PRO features]
```

---

## 6. MVP Feature Scope

### INCLUDE (Ship This Week)
- ✅ User registration + login (JWT)
- ✅ Document upload + text extraction (PDF/DOCX)
- ✅ Claude API analysis
- ✅ Verdict system (🟢 Seguro | 🟡 Atenção | 🔴 Cuidado)
- ✅ Trial system (7-day limit, 5 docs/month free)
- ✅ "Upgrade PRO" form + email notification
- ✅ Railway deployment + domain setup

### EXCLUDE (Ship Later)
- ❌ Stripe payment integration (manual for now)
- ❌ Automated email confirmations (you respond manually)
- ❌ Advanced analytics dashboard
- ❌ Multi-language support (Portuguese only)
- ❌ Rate limiting (add if needed post-launch)
- ❌ Mobile app (web only)

---

## 7. Success Metrics (Week 1)

- **Instagram:** 50+ new followers, 10+ link clicks
- **App:** 20+ sign-ups, 10+ trial activations
- **Conversions:** 3+ upgrade requests (30% conversion is win)
- **No critical bugs** in production

---

## 8. Risks & Mitigation

| Risk | Mitigation |
|------|-----------|
| Deploy breaks in production | Test locally first, deploy Friday morning, monitor 24h |
| Instagram account flagged as spam | Start slow (2-3 posts/day), engage genuinely, don't auto-post |
| No one converts | Have backup: email newsletter + direct outreach to early users |
| Claude API rate limits | Set quota check in backend, warn user before hitting limit |
| Payment processing manual is slow | Set expectation: "Upgrade em 24h via email" |

---

## 9. Implementation Order (7 Days)

**Day 1 (Today):** 
- Finalize design, commit this doc
- Start deployment setup

**Day 2-3:**
- Deploy to Railway
- Test in production
- Finalize Instagram content

**Day 4:**
- Launch Instagram account + first posts
- Monitor feedback

**Day 5-6:**
- Grow content, respond to engagement
- Monitor signup funnel

**Day 7:**
- Wrap-up, optimization, monitor metrics

---

## 10. Post-Launch (Week 2+)

- **If good traction:** Integrate Stripe (automate checkout)
- **If mediocre:** Double down on content, try TikTok
- **If no traction:** Pivot to different audience (advogados, contadores)

---

**Owner:** Conrado Moura  
**Status:** Ready for implementation
