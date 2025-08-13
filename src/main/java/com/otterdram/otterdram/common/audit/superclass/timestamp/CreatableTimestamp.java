package com.otterdram.otterdram.common.audit.superclass.timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * <pre>
 * createdAt
 * </pre>
 */

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class CreatableTimestamp {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz(6)")
    private Instant createdAt;
}
