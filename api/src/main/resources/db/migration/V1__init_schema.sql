-- ============================================================
-- V1 - Schema inicial Heim API
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    user_id        BIGSERIAL PRIMARY KEY,
    full_name      VARCHAR(255),
    email          VARCHAR(255),
    password       VARCHAR(255),
    document       VARCHAR(255),
    phone          VARCHAR(255),
    url_avatar_profile VARCHAR(255),
    role           VARCHAR(50),
    created_at     TIMESTAMP,
    is_active      BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS drivers (
    id                      BIGSERIAL PRIMARY KEY,
    license_number          VARCHAR(255),
    license_category        VARCHAR(255),
    vehicle_type            VARCHAR(255),
    enroll_vehicle          VARCHAR(255),
    promotional_moves_left  INTEGER,
    created_at              TIMESTAMP,
    status                  VARCHAR(50) NOT NULL,
    user_id                 BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_driver_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS moves (
    move_id         BIGSERIAL PRIMARY KEY,
    origin          VARCHAR(255),
    destination     VARCHAR(255),
    origin_lat      DOUBLE PRECISION,
    origin_lng      DOUBLE PRECISION,
    destination_lat DOUBLE PRECISION,
    destination_lng DOUBLE PRECISION,
    type_of_move    VARCHAR(50),
    price           NUMERIC(10, 2),
    payment_method  VARCHAR(255),
    request_time    TIMESTAMP,
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    distance_km     VARCHAR(255),
    duration_min    VARCHAR(255),
    access_type     VARCHAR(255),
    status          VARCHAR(50),
    payment_status  VARCHAR(50),
    driver_id       BIGINT,
    user_id         BIGINT,
    CONSTRAINT fk_move_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_move_user   FOREIGN KEY (user_id)   REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS admin (
    admin_id        BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    email           VARCHAR(255),
    password        VARCHAR(255),
    role            VARCHAR(50),
    creation_date   TIMESTAMP,
    is_active       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS password_reset (
    id              BIGSERIAL PRIMARY KEY,
    token           VARCHAR(100) NOT NULL,
    used            BOOLEAN NOT NULL DEFAULT FALSE,
    expiration_time TIMESTAMP,
    created_at      TIMESTAMP,
    user_id         BIGINT NOT NULL,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS fcm_tokens (
    token_id    BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) UNIQUE,
    owner_id    BIGINT,
    owner_type  VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_owner ON fcm_tokens (owner_id, owner_type);

CREATE TABLE IF NOT EXISTS payments (
    id                BIGSERIAL PRIMARY KEY,
    move_id           BIGINT,
    user_id           BIGINT,
    amount            NUMERIC(19, 2),
    provider          VARCHAR(255),
    provider_order_id BIGINT,
    status            VARCHAR(50),
    method            VARCHAR(50),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);

CREATE TABLE IF NOT EXISTS driver_payment_account (
    id             BIGSERIAL PRIMARY KEY,
    driver_id      BIGINT NOT NULL UNIQUE,
    payment_method VARCHAR(20) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    is_primary     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS earning (
    earning_id       BIGSERIAL PRIMARY KEY,
    is_settled       BOOLEAN NOT NULL DEFAULT FALSE,
    net_amount       NUMERIC(19, 2),
    settlement_date  TIMESTAMP,
    creation_date    TIMESTAMP,
    driver_id        BIGINT,
    move_id          BIGINT UNIQUE,
    CONSTRAINT fk_earning_driver FOREIGN KEY (driver_id) REFERENCES drivers(id),
    CONSTRAINT fk_earning_move   FOREIGN KEY (move_id)   REFERENCES moves(move_id)
);

CREATE TABLE IF NOT EXISTS payouts (
    id          BIGSERIAL PRIMARY KEY,
    driver_id   BIGINT,
    amount      NUMERIC(19, 2),
    reference   VARCHAR(255),
    created_at  TIMESTAMP,
    status      VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS driver_balance (
    id                BIGSERIAL PRIMARY KEY,
    driver_id         BIGINT NOT NULL UNIQUE,
    available_balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    last_updated_at   TIMESTAMP NOT NULL
);
