-- =========================================
-- V6: Create books table
-- =========================================

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,

                       isbn VARCHAR(20) NOT NULL UNIQUE,

                       title VARCHAR(200) NOT NULL,

                       published_year INTEGER,

                       price NUMERIC(10, 2) NOT NULL,

                       author_id BIGINT NOT NULL,

                       category_id BIGINT NOT NULL,

                       CONSTRAINT fk_books_author
                           FOREIGN KEY (author_id)
                               REFERENCES authors(id),

                       CONSTRAINT fk_books_category
                           FOREIGN KEY (category_id)
                               REFERENCES categories(id)
);