package com.otterdram.otterdram.common.audit.superclass;

import jakarta.persistence.Column;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * modifiedAt
 * updatedAt + updatedBy
 * deletedAt + deletedBy
 * </pre>
 */
public abstract class AuthorModifiable extends SoftDeletable {

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public boolean isModified() {
        return modifiedAt != null;
    }

    public void markModified() {
        this.modifiedAt = Instant.now();
    }
}
