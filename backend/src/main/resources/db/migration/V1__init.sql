-- FinPulse schema

CREATE TABLE IF NOT EXISTS portfolios (
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(100) NOT NULL,
    name        VARCHAR(200) NOT NULL DEFAULT 'My Portfolio',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_portfolios_user ON portfolios(user_id);

CREATE TABLE IF NOT EXISTS holdings (
    id                  BIGSERIAL PRIMARY KEY,
    portfolio_id        BIGINT       NOT NULL REFERENCES portfolios(id) ON DELETE CASCADE,
    symbol              VARCHAR(20)  NOT NULL,
    quantity            NUMERIC(18,6) NOT NULL,
    avg_cost_per_share  NUMERIC(18,4) NOT NULL,
    purchased_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_holdings_portfolio ON holdings(portfolio_id);

CREATE TABLE IF NOT EXISTS watchlist (
    id       BIGSERIAL PRIMARY KEY,
    user_id  VARCHAR(100) NOT NULL,
    symbol   VARCHAR(20)  NOT NULL,
    added_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, symbol)
);

CREATE INDEX idx_watchlist_user ON watchlist(user_id);

-- Seed demo portfolio
INSERT INTO portfolios (user_id, name) VALUES ('demo-user', 'My Portfolio')
    ON CONFLICT DO NOTHING;

INSERT INTO holdings (portfolio_id, symbol, quantity, avg_cost_per_share)
SELECT 1, 'AAPL', 10, 150.00 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id=1 AND symbol='AAPL');
INSERT INTO holdings (portfolio_id, symbol, quantity, avg_cost_per_share)
SELECT 1, 'MSFT', 5, 380.00 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id=1 AND symbol='MSFT');
INSERT INTO holdings (portfolio_id, symbol, quantity, avg_cost_per_share)
SELECT 1, 'NVDA', 8, 600.00 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id=1 AND symbol='NVDA');
INSERT INTO holdings (portfolio_id, symbol, quantity, avg_cost_per_share)
SELECT 1, 'GOOGL', 3, 140.00 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id=1 AND symbol='GOOGL');

INSERT INTO watchlist (user_id, symbol) VALUES
    ('demo-user', 'AAPL'), ('demo-user', 'MSFT'), ('demo-user', 'GOOGL'),
    ('demo-user', 'NVDA'), ('demo-user', 'TSLA'), ('demo-user', 'AMZN')
ON CONFLICT DO NOTHING;
