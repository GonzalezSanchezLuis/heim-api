CREATE TABLE IF NOT EXISTS move_pricing (
    id BIGSERIAL PRIMARY KEY,
    move_id BIGINT NOT NULL UNIQUE,
    base_price NUMERIC(10, 2) NOT NULL,
    discount_percentage INT NOT NULL DEFAULT 0,
    discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    final_price NUMERIC(10, 2) NOT NULL,
    is_first_trip BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_move_pricing_move FOREIGN KEY (move_id) REFERENCES moves(move_id)
);
