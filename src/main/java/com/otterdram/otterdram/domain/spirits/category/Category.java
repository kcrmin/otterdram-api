package com.otterdram.otterdram.domain.spirits.category;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.cask.Cask;
import com.otterdram.otterdram.domain.spirits.model.Model;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Category Entity
 * <pre>
 * Table categories {
 *   id bigint [pk, increment]
 *   name varchar(100) [not null, note: "카테고리명, 예: 'Single Malt', 'Bourbon', 'Blended'"]
 *   parent_id bigint [ref: > categories.id, default: null]
 *   depth smallint [not null, note: "카테고리 깊이, 루트=0, 예: 1=1단계, 2=2단계"]
 *   path varchar(255) [not null, note: "materialized path, 예: '1/2/3'"]
 *   translations jsonb [note: "다국어 JSON"]
 *   descriptions jsonb [note: "다국어 JSON"]
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
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "category_seq", sequenceName = "category_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parentCategory;

    @Column(name = "depth", nullable = false)
    private Short depth;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "jsonb")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "jsonb")
    private Map<LanguageCode, String> descriptions;

    // =========================== Relationships ===========================

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Cask> casks = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Model> models = new ArrayList<>();
}
