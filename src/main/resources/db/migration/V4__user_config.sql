CREATE TABLE user_config (
  user_id BIGINT PRIMARY KEY,
  closing_day INT NOT NULL DEFAULT 5,
  available_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT now(),

  CONSTRAINT fk_user_config_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON DELETE CASCADE,

  CONSTRAINT chk_closing_day
    CHECK (closing_day >= 1 AND closing_day <= 31)
);

CREATE INDEX idx_user_config_user_id ON user_config(user_id);
