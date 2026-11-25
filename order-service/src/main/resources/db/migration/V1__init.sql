CREATE TABLE orders (
    id             UUID PRIMARY KEY,
    customer_email VARCHAR(255)  NOT NULL,
    amount         NUMERIC(12,2) NOT NULL,
    status         VARCHAR(20)   NOT NULL,
    created_at     TIMESTAMPTZ   NOT NULL
);

CREATE TABLE outbox_events (
    id           UUID PRIMARY KEY,
    aggregate_id UUID         NOT NULL,
    event_type   VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    published    BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished ON outbox_events (created_at) WHERE published = FALSE;
