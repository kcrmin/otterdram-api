package com.otterdram.otterdram.domain.spirits.cask;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Cask Material Entity
 * <pre>
 * Table cask_materials {
 *   id bigint [pk, increment]
 *   name varchar(50) [not null, unique, note: "예: American Oak, European Oak, Mizunara 등"]
 *   translations jsonb [note: "다국어 지원"]
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
@Table(name = "cask_materials")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaskMaterial extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "cask_material_seq", sequenceName = "cask_material_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cask_material_seq")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

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
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cask> casks = new ArrayList<>();
}
