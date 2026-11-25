
package com.company.bookhub.book.messaging.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ProcessedBulkBatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public int claimBatch(UUID batchId) {
        return entityManager.createNativeQuery("""
                INSERT INTO processed_bulk_batches (
                    batch_id,
                    received_count,
                    added_count,
                    failed_count
                )
                VALUES (:batchId, 0, 0, 0)
                ON CONFLICT (batch_id) DO NOTHING
                """)
                .setParameter("batchId", batchId)
                .executeUpdate();
    }

    public int updateBatchResult(
            UUID batchId,
            int receivedCount,
            int addedCount,
            int failedCount
    ) {
        return entityManager.createNativeQuery("""
                UPDATE processed_bulk_batches
                SET received_count = :receivedCount,
                    added_count = :addedCount,
                    failed_count = :failedCount,
                    processed_at = CURRENT_TIMESTAMP
                WHERE batch_id = :batchId
                """)
                .setParameter("batchId", batchId)
                .setParameter("receivedCount", receivedCount)
                .setParameter("addedCount", addedCount)
                .setParameter("failedCount", failedCount)
                .executeUpdate();
    }
}
