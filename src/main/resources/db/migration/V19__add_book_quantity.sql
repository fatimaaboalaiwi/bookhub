-- Add inventory quantity to books.
-- Existing books receive an initial quantity of 1.

ALTER TABLE books
    ADD COLUMN qty INTEGER NOT NULL DEFAULT 1;

-- Prevent negative inventory quantities.

ALTER TABLE books
    ADD CONSTRAINT chk_books_qty_non_negative
        CHECK (qty >= 0);