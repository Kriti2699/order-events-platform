CREATE TABLE notifications (
    id         UUID PRIMARY KEY,
    order_id   UUID         NOT NULL,
    recipient  VARCHAR(255) NOT NULL,
    message    TEXT         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL
);

-- Idempotency table: one row per event we have already handled.
CREATE TABLE processed_events (
    event_id     UUID PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL
);
