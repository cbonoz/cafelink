-- Author: Chris Buonocore
-- Cafelink SQL schema setup code

-- DROP DATABASE IF EXISTS cafe;
CREATE DATABASE cafe;

\c cafe;


CREATE TABLE users (
  ID VARCHAR PRIMARY KEY,
  email VARCHAR NOT NULL,
  address VARCHAR,
  username VARCHAR(16),
);