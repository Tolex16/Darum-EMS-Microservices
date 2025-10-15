--INSERT INTO roles(name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
--INSERT INTO roles(name) VALUES ('MANAGER') ON CONFLICT DO NOTHING;
--INSERT INTO roles(name) VALUES ('EMPLOYEE') ON CONFLICT DO NOTHING;
-- Seed admin user (password: admin) -> you should re-hash in real app
INSERT INTO users(username, email, password)
VALUES ('admin','admin@local','{noop}admin')
ON CONFLICT DO NOTHING;
