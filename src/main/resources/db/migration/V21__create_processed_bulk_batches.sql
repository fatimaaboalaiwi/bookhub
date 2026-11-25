CREATE TABLE processed_bulk_batches (
                                        batch_id UUID PRIMARY KEY,
                                        received_count INTEGER NOT NULL DEFAULT 0,
                                        added_count INTEGER NOT NULL DEFAULT 0,
                                        failed_count INTEGER NOT NULL DEFAULT 0,
                                        processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);