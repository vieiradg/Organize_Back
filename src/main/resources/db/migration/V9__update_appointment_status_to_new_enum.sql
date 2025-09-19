CREATE TYPE appointment_status_new AS ENUM (
    'PENDING',
    'CONFIRMED',
    'RESCHEDULED',
    'CANCELED',
    'REJECTED',
    'COMPLETED',
    'NO_SHOW'
);

ALTER TABLE appointments ALTER COLUMN status DROP DEFAULT;

ALTER TABLE appointments
ALTER COLUMN status TYPE appointment_status_new
USING status::text::appointment_status_new;

ALTER TABLE appointments ALTER COLUMN status SET DEFAULT 'PENDING';

DROP TYPE IF EXISTS appointment_status;

ALTER TYPE appointment_status_new RENAME TO appointment_status;
