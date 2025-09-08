package com.otterdram.otterdram.domain.spirits.model;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.category.Category;
import com.otterdram.otterdram.domain.spirits.collection.Collection;
import com.otterdram.otterdram.domain.spirits.revision.RevisableEntity;
import com.otterdram.otterdram.domain.spirits.release.Release;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Model Entity
 * <pre>
 * Table models {
 *   id bigint [pk, increment]
 *   collection_id bigint [ref: > collections.id, not null]
 *   category_id bigint [ref: > categories.id, note: "예: 버번, 블랜디드 등"]
 *   model_image varchar(255)
 *   model_name varchar(100) [not null]
 *   translations jsonb [note: "다국어 지원 이름"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   status DataStatus [not null, default: 'IN_REVIEW']
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 *   deleted_at timestamp
 *   deleted_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Entity
@Table(name = "models")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Model extends SoftDeletable /*implements RevisableEntity*/ {

    @Id
    @SequenceGenerator(name = "model_seq", sequenceName = "model_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "model_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "model_image", length = 255)
    private String modelImage;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'IN_REVIEW'")
    private DataStatus status = DataStatus.IN_REVIEW;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<Release> releases = new ArrayList<>();
}
