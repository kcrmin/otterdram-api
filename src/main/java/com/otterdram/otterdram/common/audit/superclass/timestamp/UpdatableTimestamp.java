package com.otterdram.otterdram.common.audit.superclass.timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * <pre>
 * createdAt
 * updatedAt
 * </pre>
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class UpdatableTimestamp extends CreatableTimestamp {

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz(6)")
    private Instant updatedAt;

}
