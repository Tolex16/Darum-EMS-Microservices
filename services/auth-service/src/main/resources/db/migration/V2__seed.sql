-- Seed admin user (hashed password should be rotated later)
INSERT INTO users (email, password, role, status)
VALUES ('darumems@gmail.com', '{noop}Winner123!', 'ADMIN', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;