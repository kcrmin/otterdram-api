package com.otterdram.otterdram.domain.spirits.collection;

import com.otterdram.otterdram.common.audit.superclass.SoftDeletable;
import com.otterdram.otterdram.common.enums.common.DataStatus;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.spirits.brand.Brand;
import com.otterdram.otterdram.domain.spirits.model.Model;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collection Entity
 * <pre>
 * Table collections {
 *   id bigint [pk, increment]
 *   brand_id bigint [ref: > brands.id, not null]
 *   collection_name varchar(100) [not null]
 *   translations text [note: "다국어 지원 이름"]
 *   descriptions text [note: "다국어 지원"]
 *   status DataStatus [not null, default: 'DRAFT']
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
@Table(name = "collections")
public class Collection extends SoftDeletable {

    @Id
    @SequenceGenerator(name = "collection_seq", sequenceName = "collection_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "collection_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "collection_name", nullable = false, length = 100)
    private String collectionName;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "json")
    private Map<LanguageCode, String> translations;

    @Type(JsonType.class)
    @Column(name = "descriptions", columnDefinition = "json")
    private Map<LanguageCode, String> descriptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'DRAFT'")
    private DataStatus status = DataStatus.DRAFT;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    private List<Model> models = new ArrayList<>();

}
