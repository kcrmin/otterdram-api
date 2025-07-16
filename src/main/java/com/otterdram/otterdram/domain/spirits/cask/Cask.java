package com.otterdram.otterdram.domain.spirits.cask;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.category.Category;
import com.otterdram.otterdram.domain.spirits.relation.ReleaseCaskRelation;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Cask Entity
 * <pre>
 * Table casks {
 *   id bigint [pk, increment]
 *   name varchar(100) [not null, unique, note: "예: PX Sherry Butt, Bourbon Barrel 등"]
 *   category_id bigint [ref: > categories.id, note: "예: 버번, 셰리와인 등"]
 *   material_id bigint [ref: > cask_materials.id]
 *   type_id bigint [ref: > cask_types.id]
 *   translations jsonb [note: "다국어 지원"]
 *   descriptions jsonb [note: "다국어 지원"]
 *   status DataStatus [not null, default: 'DRAFT']
 *   created_at timestamp [not null]
 *   created_by bigint [ref: > users.id, not null]
 *   updated_at timestamp [not null]
 *   updated_by bigint [ref: > users.id, not null]
 *   deleted_at timestamp
 *   deleted_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Entity
@Table(name = "casks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cask extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "cask_seq", sequenceName = "cask_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cask_seq")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private CaskMaterial material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CaskType type;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'DRAFT'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "cask", fetch = FetchType.LAZY)
    private List<ReleaseCaskRelation> releaseCaskRelations = new ArrayList<>();
}
