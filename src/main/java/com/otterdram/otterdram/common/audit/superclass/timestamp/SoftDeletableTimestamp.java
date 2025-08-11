package com.otterdram.otterdram.common.audit.superclass.timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * <pre>
 * createdAt
 * updatedAt
 * deletedAt
 * </pre>
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SoftDeletableTimestamp {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz(6)")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz(6)")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "timestamptz(6)")
    private Instant deletedAt;
}
