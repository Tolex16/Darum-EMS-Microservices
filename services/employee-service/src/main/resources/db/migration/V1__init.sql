CREATE TABLE IF NOT EXISTS departments (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL UNIQUE,
  code VARCHAR(20) NOT NULL UNIQUE,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS employees (
  employee_id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name  VARCHAR(100) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  department_id BIGINT NOT NULL REFERENCES departments(id),
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_employees_dept ON employees(department_id);
