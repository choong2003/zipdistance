-- Create DB
--CREATE DATABASE zipdistance;


-- Create a role with login and password
--CREATE ROLE testadmin WITH LOGIN PASSWORD 'testadmin' SUPERUSER;

CREATE TABLE IF NOT EXISTS postcodelatlng (
  id SERIAL PRIMARY KEY,
  postcode VARCHAR(8) NOT NULL,
  latitude DECIMAL(10, 7),
  longitude DECIMAL(10, 7)
);
