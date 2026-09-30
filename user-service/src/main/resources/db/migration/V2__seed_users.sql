-- Seed Roles
INSERT INTO roles (id, name) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_STAFF'),
(3, 'ROLE_RESIDENT')
ON CONFLICT (name) DO NOTHING;

-- Seed Sample Apartments for Society 1
INSERT INTO apartments (id, block_name, flat_number, society_id) VALUES 
(1, 'A', '101', 1),
(2, 'B', '204', 1)
ON CONFLICT (society_id, block_name, flat_number) DO NOTHING;

-- Seed Users with Password@123 ($2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i)
-- 1. Admin
INSERT INTO users (id, email, password_hash, full_name, phone_number, society_id, is_active, is_available) VALUES 
(1, 'admin@smartsociety.com', '$2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i', 'Society Administrator', '+91-9876543210', 1, true, true)
ON CONFLICT (email) DO NOTHING;

-- 2. Staff (Plumbing & Electrical)
INSERT INTO users (id, email, password_hash, full_name, phone_number, society_id, department, is_active, is_available) VALUES 
(2, 'plumber@smartsociety.com', '$2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i', 'Ramesh Sharma (Plumber)', '+91-9876543211', 1, 'PLUMBING', true, true),
(3, 'electrician@smartsociety.com', '$2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i', 'Suresh Verma (Electrician)', '+91-9876543212', 1, 'ELECTRICAL', true, true)
ON CONFLICT (email) DO NOTHING;

-- 3. Residents
INSERT INTO users (id, email, password_hash, full_name, phone_number, society_id, apartment_id, is_active, is_available) VALUES 
(4, 'resident1@smartsociety.com', '$2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i', 'Amit Kumar', '+91-9876543213', 1, 1, true, true),
(5, 'resident2@smartsociety.com', '$2a$10$91H.szPrSbIbUK13UnergeH0476D5161CHBzU/Ih1s.1WEFe0uh9i', 'Priya Singh', '+91-9876543214', 1, 2, true, true)
ON CONFLICT (email) DO NOTHING;

-- Seed User-Role mappings
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- Admin -> ROLE_ADMIN
(2, 2), -- Plumber -> ROLE_STAFF
(3, 2), -- Electrician -> ROLE_STAFF
(4, 3), -- Resident 1 -> ROLE_RESIDENT
(5, 3)  -- Resident 2 -> ROLE_RESIDENT
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Reset sequence IDs
SELECT setval('apartments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM apartments));
SELECT setval('roles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM roles));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
