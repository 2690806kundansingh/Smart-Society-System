-- Initialize separate microservice databases
SELECT 'CREATE DATABASE society_users'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'society_users')\gexec

SELECT 'CREATE DATABASE society_complaints'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'society_complaints')\gexec

GRANT ALL PRIVILEGES ON DATABASE society_users TO postgres;
GRANT ALL PRIVILEGES ON DATABASE society_complaints TO postgres;
