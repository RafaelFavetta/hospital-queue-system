-- Hospital Queue System - Database Schema

-- Priority levels table
CREATE TABLE IF NOT EXISTS priority_levels (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE,
    level INT NOT NULL UNIQUE
);

INSERT INTO priority_levels (name, level) VALUES
    ('LOW', 1),
    ('MEDIUM', 2),
    ('HIGH', 3),
    ('EXTREME', 4)
ON CONFLICT (name) DO NOTHING;

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    priority_level_id INT NOT NULL,
    arrival_order BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_priority_level
        FOREIGN KEY (priority_level_id)
        REFERENCES priority_levels(id)
);

-- Queue table
CREATE TABLE IF NOT EXISTS queue (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(26) NOT NULL UNIQUE,
    priority_score INT NOT NULL,
    arrival_order BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_queue_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE
);

-- Queue history table
CREATE TABLE IF NOT EXISTS queue_history (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(26) NOT NULL,
    action VARCHAR(20) NOT NULL,
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
);


