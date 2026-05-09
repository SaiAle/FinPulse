# FinPulse — AI-Powered Real-Time Financial Analytics

[![CI/CD](https://github.com/SaiAle/FinPulse/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/SaiAle/FinPulse/actions)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Real-time stock price streaming, AI-generated market insights, and portfolio P&L tracking — all in one dark-mode dashboard.

## Key Features

| Feature | Description |
|---|---|
| **Live Price Streaming** | WebSocket-powered real-time quotes with price flash animations |
| **AI Market Insights** | GPT-4o-mini analyzes price action and returns BUY/SELL/HOLD signals |
| **Portfolio Tracker** | Holdings, cost basis, live P&L, and allocation breakdown |
| **Watchlist** | Add/remove symbols, prices refresh every 5 seconds |
| **Fallback Mode** | Works without API keys using realistic simulated data |
| **TimescaleDB** | Time-series optimized PostgreSQL for price history |
| **Circuit Breaker** | Resilience4j — AI service degrades gracefully |
| **Observability** | Prometheus metrics + health probes |

## Quick Start

```bash
git clone https://github.com/SaiAle/FinPulse.git
cd FinPulse
cp .env.example .env
# Optionally add: ALPHA_VANTAGE_API_KEY and OPENAI_API_KEY
docker compose up --build

# Open http://localhost:3000
```

> Works without API keys — the backend serves realistic simulated prices.

## Tech Stack

| Layer | Technology |
|---|---|
| **API** | Spring Boot 3.2, Spring WebFlux, Project Reactor |
| **WebSocket** | Spring Reactive WebSocket (real-time price streaming) |
| **AI** | LangChain4j 0.29, OpenAI GPT-4o-mini |
| **Market Data** | Alpha Vantage API (+ fallback simulation) |
| **Database** | TimescaleDB (PostgreSQL 16), R2DBC reactive |
| **Cache** | Redis 7 — 15-second quote cache |
| **Resilience** | Resilience4j Circuit Breaker |
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS, Recharts |
| **CI/CD** | GitHub Actions, Docker Buildx, GHCR |

## Author

**Sai Kumar Ale**
- GitHub: [@SaiAle](https://github.com/SaiAle)
- LinkedIn: [sai-kumar-a-1bb808284](https://linkedin.com/in/sai-kumar-a-1bb808284)
- Portfolio: [saiale.github.io](https://saiale.github.io)

*Part of a 3-project portfolio — see also [AsyncFlow](https://github.com/SaiAle/AsyncFlow) and [VaultSecure](https://github.com/SaiAle/VaultSecure)*
