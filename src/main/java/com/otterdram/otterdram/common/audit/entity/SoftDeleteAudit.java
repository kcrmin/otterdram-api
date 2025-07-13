package com.otterdram.otterdram.common.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * <pre>
 * deletedAt + deletedBy
 * </pre>
 */
@Embeddable
public class SoftDeleteAudit {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markAsDeleted(Long deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }

}
