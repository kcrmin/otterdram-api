package com.otterdram.otterdram.common.geo.state;

import com.otterdram.otterdram.common.geo.city.City;
import com.otterdram.otterdram.common.geo.country.Country;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.List;

/** State Entity
 * <pre>
 * Table states {
 *   id bigint [pk, not null, increment]
 *   name varchar(255) [not null, note: "주/도 명칭, 예: 'California', '서울특별시'"]
 *   country_id bigint [not null, ref: > countries.id]
 *   country_code char(2) [not null, note: "ISO 국가코드, 예: 'US', 'KR'"]
 *   fips_code varchar(255) [note: "미국 등 FIPS 코드, 예: 'CA'"]
 *   iso2 varchar(255) [note: "ISO 2자리 주/도 코드, 예: 'CA', '11'"]
 *   type varchar(191) [note: "행정구역 종류, 예: 'State', 'Province', 'Special City'"]
 *   level integer [note: "행정 단계, 예: 1=광역, 2=시/군 등"]
 *   parent_id integer
 *   latitude numeric(10,8)
 *   longitude numeric(11,8)
 *   created_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   flag smallint [not null, default: 1, note: "0=비활성, 1=활성"]
 *   "wikiDataId" varchar(255) [note: "위키데이터 Q번호, 예: 'Q485705'"]
 * }
 * </pre>
 */

@Entity
@Immutable
@Table(name = "states", schema = "public")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class State {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, insertable = false, updatable = false)
    private Country country;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "fips_code", length = 255)
    private String fipsCode;

    @Column(name = "iso2", length = 255)
    private String iso2;

    @Column(name = "type", length = 191)
    private String type;

    @Column(name = "level", nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private State parentState;

    @Column(name = "latitude", precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private Double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false, columnDefinition = "smallint default 1")
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

    // Child States relationship
    @Immutable
    @OneToMany(mappedBy = "parentState", fetch = FetchType.LAZY)
    private List<State> childStates;


    // City relationship
    @Immutable
    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<City> cities;



}
