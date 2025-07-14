package com.otterdram.otterdram.common.audit.superclass;

import com.otterdram.otterdram.common.enums.DataStatus;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * reviewedAt + reviewedBy
 * </pre>
 */
@MappedSuperclass
public abstract class Reviewable extends Creatable {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    private DataStatus status = DataStatus.PENDING;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    public boolean isReviewed() {
        return this.reviewedAt != null && this.reviewedBy != null;
    }

    public boolean isPending() {
        return this.status == DataStatus.PENDING;
    }

    public void review(Long userId, DataStatus status) {
        if (!isReviewed() && isPending()) {
            this.reviewedAt = Instant.now();
            this.reviewedBy = userId;
            this.status = status;
        } else {
            throw new IllegalStateException("Cannot review an already reviewed or non-pending item.");
        }
    }

}
