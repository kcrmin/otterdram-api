package com.otterdram.otterdram.common.audit.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.Instant;

/**
 * <pre>
 * createdAt + createdBy
 * updatedAt + updatedBy
 * reviewedAt + reviewedBy
 * </pre>
 */
@MappedSuperclass
public abstract class Reviewable extends Updatable {

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

}
