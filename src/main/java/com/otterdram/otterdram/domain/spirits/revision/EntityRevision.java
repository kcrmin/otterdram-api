package com.otterdram.otterdram.domain.spirits.revision;

import com.otterdram.otterdram.common.audit.superclass.Reviewable;
import com.otterdram.otterdram.common.enums.target.RevisionTargetEntity;
import com.otterdram.otterdram.common.enums.common.RevisionStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * <pre>
 * Table revisions {
 *   id bigint [pk, increment]
 *   entity_type RevisionEntityType [not null, note: "수정 대상 엔티티 종류]
 *   entity_id bigint [not null, note: "수정 대상 엔티티 ID"]
 *   schema_version varchar(16) [not null, default: '1.0']
 *   revision_data jsonb [not null]
 *   diff_data jsonb
 *   is_latest boolean [not null, default: true, note: "생성 시 이전 버전 값 false로 변경"]
 *   status RevisionStatus [not null, default: 'IN_REVIEW']
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   reviewed_at timestamp
 *   reviewed_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Getter
@SuperBuilder
@Entity
@Table(name = "revisions")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityRevision extends Reviewable {

    @Id
    @SequenceGenerator(name = "revision_seq", sequenceName = "revision_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, columnDefinition = "varchar(50)")
    private RevisionTargetEntity entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Builder.Default
    @Column(name = "schema_version", nullable = false, length = 16, columnDefinition = "varchar(16) default '1.0'")
    private String schemaVersion = "1.0.0";

    @Type(JsonType.class)
    @Column(name = "revision_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> revisionData;

    @Type(JsonType.class)
    @Column(name = "diff_data", columnDefinition = "jsonb")
    private Map<String, Object> diffData;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private RevisionStatus status = RevisionStatus.IN_REVIEW;

}
