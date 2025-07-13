package com.otterdram.otterdram.common.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * <pre>
 * reviewedAt + reviewedBy
 * </pre>
 */
@Embeddable
public class ReviewAudit {

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    public boolean isReviewed() {
        return reviewedAt != null;
    }

    public void markAsReviewed(Long reviewedBy) {
        this.reviewedAt = Instant.now();
        this.reviewedBy = reviewedBy;
    }

}
