CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE role_type AS ENUM ('ADMIN', 'PROFESSIONAL', 'CUSTOMER');

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role role_type NOT NULL,

    CONSTRAINT fk_user
        FOREIGN KEY(user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

