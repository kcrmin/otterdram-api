package com.otterdram.otterdram.domain.spirits.tag;

import com.otterdram.otterdram.common.audit.superclass.Creatable;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.target.TagTargetEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Tag Entity
 * <pre>
 * Table entity_tags {
 *   id bigint [pk, increment]
 *   entity_type TagEntityType [not null, note: "태그 대상 엔티티 종류"]
 *   entity_id bigint [not null, note: "태그 대상 엔티티 ID"]
 *   language_code LanguageCode [not null]
 *   tag varchar(50) [not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 * }
 * </pre>
 */

@Entity
@Table(name = "entity_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityTag extends Creatable {

    @Id
    @SequenceGenerator(name = "tag_seq", sequenceName = "tag_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, columnDefinition = "varchar(50)")
    private TagTargetEntity entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, columnDefinition = "varchar(10) default 'EN'")
    private LanguageCode languageCode = LanguageCode.EN;

    @Column(name = "tag", nullable = false, length = 50)
    private String tag;
}
