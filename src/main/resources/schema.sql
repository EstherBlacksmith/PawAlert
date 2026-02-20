-- PawAlert Database Schema
-- Updated to match entity classes and improvements document

-- Users table
-- Added: surname, role, telegram_chat_id, email_notifications_enabled, telegram_notifications_enabled
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    surname VARCHAR(255),
    phone_number VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    telegram_chat_id VARCHAR(100),
    email_notifications_enabled BOOLEAN DEFAULT FALSE,
    telegram_notifications_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

-- Alerts table
CREATE TABLE IF NOT EXISTS alerts (
    id VARCHAR(255) PRIMARY KEY,
    pet_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Alert events table
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
    longitude DOUBLE PRECISION,
    FOREIGN KEY (alert_id) REFERENCES alerts (id)
);

-- Pets table
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

-- Alert subscriptions table
CREATE TABLE IF NOT EXISTS alert_subscriptions (
   id UUID PRIMARY KEY,
   alert_id UUID NOT NULL,
   user_id UUID NOT NULL,
   subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

   -- Avoid duplicated subscriptions
   CONSTRAINT uq_alert_user UNIQUE (alert_id, user_id)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_pet_id ON alerts(pet_id);
CREATE INDEX IF NOT EXISTS idx_alerts_user ON alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_alert_events_location ON alert_events(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_alert_subscriptions_alert_id ON alert_subscriptions(alert_id);
CREATE INDEX IF NOT EXISTS idx_alert_subscriptions_user_id ON alert_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_users_telegram_chat_id ON users(telegram_chat_id);
CREATE INDEX IF NOT EXISTS idx_pets_official_name ON pets(pet_offical_name);
CREATE INDEX IF NOT EXISTS idx_pets_species ON pets(pet_species);