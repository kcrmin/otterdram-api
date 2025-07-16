package com.otterdram.otterdram.common.geo.region;

import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.geo.country.Country;
import com.otterdram.otterdram.common.geo.subregion.Subregion;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Region Entity
 * <pre>
 * Table regions {
 *   id bigint [pk, not null, increment]
 *   name varchar(100) [not null]
 *   translations text [note: "다국어 JSON, 예: {'ko':'유럽'}"]
 *   created_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   flag smallint [not null, default: 1, note: "0=비활성, 1=활성"]
 *   "wikiDataId" varchar(255) [note: "위키데이터 Q번호, 예: 'Q46'"]
 * }
 * </pre>
 */

@Entity
@Immutable
@Table(name = "regions", schema = "public")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "text")
    private Map<LanguageCode, String> translations;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false, columnDefinition = "smallint default 1")
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

    // Subregion relationship
    @Immutable
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<Subregion> subregions;

    // Country relationship
    @Immutable
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<Country> countries;



}
