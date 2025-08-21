CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL
);


CREATE TABLE establishments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    contact_phone VARCHAR(20),
    opening_hours JSONB,
    subscription_plan_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_subscription_plan
        FOREIGN KEY (subscription_plan_id) REFERENCES plans(id)
            ON DELETE SET NULL
);

CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    establishment_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price_cents INT NOT NULL,
    duration_minutes INT NOT NULL,
    requires_online_payment BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_establishment
        FOREIGN KEY(establishment_id) REFERENCES establishments(id)
            ON DELETE CASCADE
);

CREATE TABLE employees (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    establishment_id UUID NOT NULL,
    user_id UUID,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_establishment
        FOREIGN KEY(establishment_id) REFERENCES establishments(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user
        FOREIGN KEY(user_id) REFERENCES users(id)
            ON DELETE SET NULL
);

CREATE TYPE appointment_status AS ENUM (
    'CONFIRMED',
    'CANCELED',
    'COMPLETED',
    'NO_SHOW'
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id UUID NOT NULL,
    establishment_id UUID NOT NULL,
    service_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status appointment_status NOT NULL DEFAULT 'CONFIRMED',
    extra_price_cents INT DEFAULT 0,
    client_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_client
        FOREIGN KEY(client_id) REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_establishment
        FOREIGN KEY(establishment_id) REFERENCES establishments(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_service
        FOREIGN KEY(service_id) REFERENCES services(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_employee
        FOREIGN KEY(employee_id) REFERENCES employees(id)
        ON DELETE SET NULL
);

CREATE TABLE client_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id UUID NOT NULL,
    establishment_id UUID NOT NULL,
    private_notes TEXT,
    missed_appointments_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_client
        FOREIGN KEY(client_id) REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_establishment
        FOREIGN KEY(establishment_id) REFERENCES establishments(id)
            ON DELETE CASCADE
);

CREATE TYPE payment_status AS ENUM (
    'PAID',
    'PENDING',
    'REFUNDED'
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    appointment_id UUID NOT NULL,
    amount_cents INT NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    gateway_transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointment
        FOREIGN KEY(appointment_id) REFERENCES appointments(id)
            ON DELETE CASCADE
);



