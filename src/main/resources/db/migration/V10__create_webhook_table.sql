CREATE TABLE IF NOT EXISTS webhook (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    url TEXT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    CONSTRAINT fk_webhook_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX idx_webhook_event_type ON webhook(event_type);
