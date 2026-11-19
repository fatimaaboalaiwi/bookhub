
package com.company.bookhub.ai.event;

public record BookChangedEvent(
        Long bookId,
        Operation operation
) {

    public enum Operation {
        UPSERT,
        DELETE
    }
}
