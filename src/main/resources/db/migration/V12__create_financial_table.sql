CREATE TYPE transaction_status AS ENUM (
    'PAID',
    'PENDING',
    'CANCELED'
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              appointment_id UUID,
                              establishment_id UUID NOT NULL,
                              description TEXT NOT NULL,
                              amount_cents INT NOT NULL,
                              transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
                              status transaction_status NOT NULL DEFAULT 'PENDING',
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_appointment
                                  FOREIGN KEY (appointment_id)
                                      REFERENCES appointments (id)
                                      ON DELETE SET NULL
);

CREATE INDEX idx_transactions_appointment_id ON transactions (appointment_id);
CREATE INDEX idx_transactions_establishment_id ON transactions (establishment_id);
CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_transactions_date ON transactions (transaction_date);
