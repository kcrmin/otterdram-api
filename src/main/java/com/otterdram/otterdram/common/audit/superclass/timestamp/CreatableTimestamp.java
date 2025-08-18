package com.otterdram.otterdram.common.audit.superclass.timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * <pre>
 * createdAt
 * </pre>
 */

@Getter
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatableTimestamp {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz(6)")
    private Instant createdAt;
}
