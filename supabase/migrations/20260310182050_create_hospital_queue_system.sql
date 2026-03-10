/*
  # Hospital Queue System Database Schema

  ## Overview
  This migration creates the complete database structure for a hospital patient queue management system
  with priority-based ordering and real-time updates.

  ## New Tables

  ### priority_levels
  - `id` (int4, primary key) - Priority level identifier
  - `name` (text, unique) - Priority level name (LOW, MEDIUM, HIGH, EXTREME)
  - `level` (int4, unique) - Numeric priority value (1-4)
  - Purpose: Defines the 4 priority levels for patient triage

  ### patients
  - `id` (text, primary key) - ULID identifier for the patient
  - `name` (text) - Patient's full name
  - `age` (int4) - Patient's age in years
  - `priority_level_id` (int4, foreign key) - Reference to priority_levels
  - `arrival_order` (int8) - Sequential order of arrival for tie-breaking
  - `created_at` (timestamptz) - Timestamp of patient registration
  - Purpose: Stores patient information and maintains arrival order

  ### queue
  - `id` (int4, primary key) - Queue entry identifier
  - `patient_id` (text, foreign key, unique) - Reference to patients table
  - `priority_score` (int4) - Calculated priority score (base priority * 10 + elderly bonus)
  - `arrival_order` (int8) - Copy of patient's arrival order for efficient sorting
  - `added_at` (timestamptz) - Timestamp when added to queue
  - Purpose: Active queue with calculated priority scores for fast retrieval

  ### queue_history
  - `id` (int4, primary key) - History entry identifier
  - `patient_id` (text, foreign key) - Reference to patients table
  - `action` (text) - Action performed (ADDED, CALLED, REMOVED)
  - `action_timestamp` (timestamptz) - When the action occurred
  - Purpose: Audit log of all queue operations

  ## Priority Calculation Logic
  - Base score: priority_level * 10 (LOW=10, MEDIUM=20, HIGH=30, EXTREME=40)
  - Elderly bonus: +5 for patients aged 60 or older
  - Final score range: 10-45
  - Tie-breaking: Earlier arrival_order takes precedence when scores are equal

  ## Security
  - RLS enabled on all tables
  - Public read access to priority_levels (reference data)
  - Authenticated users can read all patient and queue data
  - Authenticated users can insert/update/delete patient and queue records
  - Queue history is append-only (insert only) for audit integrity

  ## Indexes
  - Primary indexes on all primary keys
  - Unique indexes on priority_levels.name and priority_levels.level
  - Unique index on queue.patient_id to prevent duplicates
  - Composite index on queue (priority_score DESC, arrival_order ASC) for optimal sorting
*/

-- Create priority_levels table
CREATE TABLE IF NOT EXISTS priority_levels (
    id INT4 PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    level INT4 NOT NULL UNIQUE
);

-- Insert priority level reference data
INSERT INTO priority_levels (id, name, level) VALUES
    (1, 'LOW', 1),
    (2, 'MEDIUM', 2),
    (3, 'HIGH', 3),
    (4, 'EXTREME', 4)
ON CONFLICT (id) DO NOTHING;

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    age INT4 NOT NULL CHECK (age >= 0 AND age <= 130),
    priority_level_id INT4 NOT NULL,
    arrival_order INT8 NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    
    CONSTRAINT fk_priority_level
        FOREIGN KEY (priority_level_id)
        REFERENCES priority_levels(id)
);

-- Create queue table
CREATE TABLE IF NOT EXISTS queue (
    id SERIAL PRIMARY KEY,
    patient_id TEXT NOT NULL UNIQUE,
    priority_score INT4 NOT NULL,
    arrival_order INT8 NOT NULL,
    added_at TIMESTAMPTZ DEFAULT now(),
    
    CONSTRAINT fk_queue_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE
);

-- Create queue_history table
CREATE TABLE IF NOT EXISTS queue_history (
    id SERIAL PRIMARY KEY,
    patient_id TEXT NOT NULL,
    action TEXT NOT NULL CHECK (action IN ('ADDED', 'CALLED', 'REMOVED')),
    action_timestamp TIMESTAMPTZ DEFAULT now(),
    
    CONSTRAINT fk_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
);

-- Create indexes for optimal query performance
CREATE INDEX IF NOT EXISTS idx_queue_priority ON queue (priority_score DESC, arrival_order ASC);
CREATE INDEX IF NOT EXISTS idx_patients_arrival ON patients (arrival_order);
CREATE INDEX IF NOT EXISTS idx_queue_history_timestamp ON queue_history (action_timestamp DESC);

-- Enable Row Level Security
ALTER TABLE priority_levels ENABLE ROW LEVEL SECURITY;
ALTER TABLE patients ENABLE ROW LEVEL SECURITY;
ALTER TABLE queue ENABLE ROW LEVEL SECURITY;
ALTER TABLE queue_history ENABLE ROW LEVEL SECURITY;

-- RLS Policies for priority_levels (reference data - public read)
CREATE POLICY "Anyone can read priority levels"
    ON priority_levels FOR SELECT
    TO public
    USING (true);

-- RLS Policies for patients
CREATE POLICY "Authenticated users can read all patients"
    ON patients FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Authenticated users can insert patients"
    ON patients FOR INSERT
    TO authenticated
    WITH CHECK (true);

CREATE POLICY "Authenticated users can update patients"
    ON patients FOR UPDATE
    TO authenticated
    USING (true)
    WITH CHECK (true);

CREATE POLICY "Authenticated users can delete patients"
    ON patients FOR DELETE
    TO authenticated
    USING (true);

-- RLS Policies for queue
CREATE POLICY "Authenticated users can read queue"
    ON queue FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Authenticated users can add to queue"
    ON queue FOR INSERT
    TO authenticated
    WITH CHECK (true);

CREATE POLICY "Authenticated users can update queue"
    ON queue FOR UPDATE
    TO authenticated
    USING (true)
    WITH CHECK (true);

CREATE POLICY "Authenticated users can remove from queue"
    ON queue FOR DELETE
    TO authenticated
    USING (true);

-- RLS Policies for queue_history (append-only audit log)
CREATE POLICY "Authenticated users can read history"
    ON queue_history FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Authenticated users can add history entries"
    ON queue_history FOR INSERT
    TO authenticated
    WITH CHECK (true);
