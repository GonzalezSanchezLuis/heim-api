CREATE TABLE IF NOT EXISTS onboarding_surveys (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    transport_need      VARCHAR(255) NOT NULL,
    registration_reason VARCHAR(255) NOT NULL,
    barrier_reason      VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP NOT NULL
);
