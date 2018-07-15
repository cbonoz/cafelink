-- Author: Chris Buonocore
-- Cafelink SQL schema setup code

-- DROP DATABASE IF EXISTS cafe;
CREATE DATABASE cafe;

\c cafe;

CREATE TABLE user (
  ID VARCHAR PRIMARY KEY,
  username VARCHAR(16),
);

CREATE TABLE conversation {
  ID VARCHAR PRIMARY KEY,
  cafeid VARCHAR NOT NULL
};

CREATE TABLE msg (
  ID VARCHAR PRIMARY KEY,
  userid VARCHAR NOT NULL references user(ID),
  conversationid VARCHAR NOT NULL references conversation(ID),
  cafeid VARCHAR NOT NULL, -- set by facebook api.
  body VARCHAR,
  timereported bigint NOT NULL,
);