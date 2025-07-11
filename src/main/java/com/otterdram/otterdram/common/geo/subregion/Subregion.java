package com.otterdram.otterdram.common.geo.subregion;

import com.otterdram.otterdram.common.enums.LanguageCode;
import com.otterdram.otterdram.common.geo.country.Country;
import com.otterdram.otterdram.common.geo.region.Region;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Subregion Entity
 * <pre>
 * Table subregions {
 *   id bigint [pk, not null, increment]
 *   name varchar(100) [not null]
 *   translations text [note: "다국어 JSON, 예: {'ko':'서유럽'}"]
 *   region_id bigint [not null, ref: > regions.id]
 *   created_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   flag smallint [not null, default: 1, note: "0=비활성, 1=활성"]
 *   "wikiDataId" varchar(255) [note: "위키데이터 Q번호, 예: 'Q990'"]
 * }
 * </pre>
 */

@Entity
@Immutable
@Table(name = "subregions", schema = "public")
public class Subregion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "text")
    private Map<LanguageCode, String> translations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false, columnDefinition = "smallint default 1")
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

    // Country relationship
    @Immutable
    @OneToMany(mappedBy = "subregion", fetch = FetchType.LAZY)
    private List<Country> countries;

}
