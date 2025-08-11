package com.otterdram.otterdram.common.audit.superclass;

import com.otterdram.otterdram.common.enums.common.RevisionStatus;
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
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private RevisionStatus status = RevisionStatus.IN_REVIEW;

    @Column(name = "reviewed_at", columnDefinition = "timestamptz(6)")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    public boolean isReviewed() {
        return this.reviewedAt != null && this.reviewedBy != null;
    }

    public boolean isPendingReview() {
        return this.status == RevisionStatus.IN_REVIEW;
    }

    public void review(Long userId, RevisionStatus status) {
        if (!isReviewed() && isPendingReview()) {
            this.reviewedAt = Instant.now();
            this.reviewedBy = userId;
            this.status = status;
        } else {
            throw new IllegalStateException("Cannot review an already reviewed or non-pending item.");
        }
    }

}
