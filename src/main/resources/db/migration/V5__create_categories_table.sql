-- =========================================
-- V5: Create categories table
-- =========================================

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,

                            name VARCHAR(100) NOT NULL UNIQUE,

                            description VARCHAR(255)
);