CREATE TABLE IF NOT EXISTS fortunes (
    id BIGSERIAL PRIMARY KEY,
    content_hash VARCHAR(64) NOT NULL UNIQUE,
    message TEXT NOT NULL,
    times_seen BIGINT NOT NULL,
    first_seen_at TIMESTAMP NOT NULL,
    last_seen_at TIMESTAMP NOT NULL
);
