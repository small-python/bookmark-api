-- Creates the users table for storing registered accounts
CREATE TABLE users (
                       id          BIGSERIAL PRIMARY KEY,

    -- User's full name
                       full_name   VARCHAR(100) NOT NULL,

    -- Used as the login identifier — must be unique across all users
                       email       VARCHAR(150) NOT NULL UNIQUE,

    -- Stores the bcrypt-hashed password — never plain text
                       password    VARCHAR(255) NOT NULL,

    -- Tracks when the account was created
                       created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Tracks the last time any field was updated
                       updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);