package com.otterdram.otterdram.common.geo.city;

import com.otterdram.otterdram.common.geo.country.Country;
import com.otterdram.otterdram.common.geo.state.State;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/** City Entity
 * <pre>
 * Table cities {
 *   id bigint [pk, not null, increment]
 *   name varchar(255) [not null, note: "도시명(영문), 예: 'Seoul', 'San Francisco'"]
 *   state_id bigint [not null, ref: > states.id]
 *   state_code varchar(255) [not null, note: "주/도 코드, 예: 'CA', '11'"]
 *   country_id bigint [not null, ref: > countries.id]
 *   country_code char(2) [not null, note: "ISO 국가코드, 예: 'US', 'KR'"]
 *   latitude numeric(10,8) [not null]
 *   longitude numeric(11,8) [not null]
 *   created_at timestamp [not null, default: `'2014-01-01 12:01:01'`]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   flag smallint [not null, default: 1, note: "0=비활성, 1=활성"]
 *   "wikiDataId" varchar(255) [note: "위키데이터 Q번호, 예: 'Q8684'"]
 * }
 * </pre>
 */
@Entity
@Immutable
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false, insertable = false, updatable = false)
    private State state;

    @Column(name = "state_code", nullable = false, length = 255)
    private String stateCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, insertable = false, updatable = false)
    private Country country;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private Double longitude;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false, columnDefinition = "smallint default 1")
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

}
