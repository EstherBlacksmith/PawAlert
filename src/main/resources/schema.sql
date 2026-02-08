CREATE TABLE IF NOT EXISTS alerts (
    id VARCHAR(255) PRIMARY KEY,
    pet_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS alert_events (
    id VARCHAR(255) PRIMARY KEY,
    alert_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    changed_by_user_id VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
    FOREIGN KEY (alert_id) REFERENCES alerts (id)
    );

CREATE TABLE IF NOT EXISTS pets (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    pet_chip_number VARCHAR(50),
    pet_offical_name VARCHAR(255),
    pet_working_name VARCHAR(255),
    pet_species VARCHAR(50),
    pet_breed VARCHAR(100),
    pet_size VARCHAR(20),
    pet_color VARCHAR(50),
    pet_gender VARCHAR(20),
    pet_description TEXT,
    pet_image TEXT,
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(50),
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS alert_subscriptions (
   id UUID PRIMARY KEY,
   alert_id UUID NOT NULL,
   user_id UUID NOT NULL,
   active BOOLEAN NOT NULL DEFAULT TRUE,
   subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   notification_channel VARCHAR(20) NOT NULL DEFAULT 'ALL',

    -- Avoid duplicated subscriptions
    CONSTRAINT uq_alert_user UNIQUE (alert_id, user_id),

    -- Frequent search indexes
    INDEX idx_alert_subscriptions_alert_id (alert_id),
    INDEX idx_alert_subscriptions_user_id (user_id),
    INDEX idx_alert_subscriptions_active (active)
    );


CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_pet ON alerts(pet_id);
CREATE INDEX IF NOT EXISTS idx_alerts_user ON alerts(user_id);

CREATE INDEX IF NOT EXISTS idx_alert_events_location ON alert_events(latitude, longitude);