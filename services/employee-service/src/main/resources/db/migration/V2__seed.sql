INSERT INTO departments(name, code) VALUES ('Engineering', 'ENG') ON CONFLICT DO NOTHING;
INSERT INTO departments(name, code) VALUES ('People Ops', 'HR') ON CONFLICT DO NOTHING;
