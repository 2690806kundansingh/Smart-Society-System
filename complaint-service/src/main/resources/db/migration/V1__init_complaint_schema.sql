-- Flyway Schema for complaint-service

-- 1. Categories
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    default_priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    default_sla_hours INT NOT NULL DEFAULT 12
);

-- Seed Categories
INSERT INTO categories (code, name, department, default_priority, default_sla_hours) VALUES
('WATER_LEAKAGE', 'Water Leakage / Pipe Burst', 'PLUMBING', 'HIGH', 2),
('LIFT_MALFUNCTION', 'Elevator / Lift Malfunction', 'ELECTRICAL', 'HIGH', 2),
('POWER_FAILURE', 'Power Failure / Circuit Tripped', 'ELECTRICAL', 'HIGH', 2),
('CLEANLINESS', 'Garbage / Cleanliness Issue', 'CLEANING', 'LOW', 48),
('PARKING', 'Unauthorized Parking / Blockage', 'GENERAL', 'MEDIUM', 12),
('GENERAL', 'General Maintenance', 'GENERAL', 'LOW', 48)
ON CONFLICT (code) DO NOTHING;

-- 2. Complaints Table with Optimistic Locking
CREATE TABLE IF NOT EXISTS complaints (
    id BIGSERIAL PRIMARY KEY,
    society_id BIGINT NOT NULL,
    resident_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location_details VARCHAR(255),
    photo_url VARCHAR(500),
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    assigned_staff_id BIGINT,
    resolution_notes TEXT,
    resolution_photo_url VARCHAR(500),
    sla_deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    sla_breached BOOLEAN NOT NULL DEFAULT FALSE,
    escalated BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- Composite and Performance Indexes
CREATE INDEX IF NOT EXISTS idx_complaints_soc_status ON complaints(society_id, status);
CREATE INDEX IF NOT EXISTS idx_complaints_sla ON complaints(sla_deadline, sla_breached);
CREATE INDEX IF NOT EXISTS idx_complaints_staff ON complaints(assigned_staff_id, status);
CREATE INDEX IF NOT EXISTS idx_complaints_resident ON complaints(resident_id);

-- 3. Audit Logs
CREATE TABLE IF NOT EXISTS complaint_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    performed_by BIGINT,
    performed_by_role VARCHAR(50),
    previous_status VARCHAR(30),
    new_status VARCHAR(30),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_complaint ON complaint_audit_logs(complaint_id);

-- 4. Feedback Ratings (Post-Resolution 5-Star Reviews)
CREATE TABLE IF NOT EXISTS feedback_ratings (
    id BIGSERIAL PRIMARY KEY,
    complaint_id BIGINT UNIQUE NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    resident_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. ShedLock Distributed Scheduling Table
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) PRIMARY KEY,
    lock_until TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_by VARCHAR(255) NOT NULL
);
