
CREATE TABLE IF NOT EXISTS alerts (
    id VARCHAR(255) PRIMARY KEY,
    pet_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
    );

CREATE TABLE IF NOT EXISTS alert_events (
    id VARCHAR(255) PRIMARY KEY,
    alert_id VARCHAR(255) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at DATETIME NOT NULL,
    changed_by_user_id VARCHAR(255),
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
    );
