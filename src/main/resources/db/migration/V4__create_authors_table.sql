-- =========================================
-- V4: Create authors table
-- =========================================

CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,

                         name VARCHAR(150) NOT NULL,

                         biography TEXT,

                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);