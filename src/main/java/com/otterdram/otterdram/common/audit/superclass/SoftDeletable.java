package com.otterdram.otterdram.common.audit.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * updatedAt + updatedBy
 * deletedAt + deletedBy
 * </pre>
 */

@Getter
@MappedSuperclass
public abstract class SoftDeletable extends Updatable {

    @Column(name = "deleted_at", columnDefinition = "timestamptz(6)")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    public boolean isDeleted() {
        return deletedAt != null && deletedBy != null;
    }

    public void softDelete(Long userId) {
        if (isDeleted()) {
            throw new IllegalStateException("Item is already deleted.");
        }
        this.deletedAt = Instant.now();
        this.deletedBy = userId;
    }

    public void restore() {
        if (!isDeleted()) {
            throw new IllegalStateException("Item is not deleted.");
        }
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
