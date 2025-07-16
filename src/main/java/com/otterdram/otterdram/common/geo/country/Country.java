package com.otterdram.otterdram.common.geo.country;

import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.geo.city.City;
import com.otterdram.otterdram.common.geo.region.Region;
import com.otterdram.otterdram.common.geo.state.State;
import com.otterdram.otterdram.common.geo.subregion.Subregion;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Country Entity
 * <pre>
 * Table countries {
 *   id bigint [pk, not null, increment]
 *   name varchar(100) [not null, note: "영문 국가명, 예: 'South Korea'"]
 *   iso3 char(3) [note: "ISO 3166-1 alpha-3, 예: 'KOR'"]
 *   numeric_code char(3) [note: "ISO 3166-1 numeric, 예: '410'"]
 *   iso2 char(2) [note: "ISO 3166-1 alpha-2, 예: 'KR'"]
 *   phonecode varchar(255) [note: "국제전화코드, 예: '82'"]
 *   capital varchar(255) [note: "수도, 예: 'Seoul'"]
 *   currency varchar(255) [note: "통화코드, 예: 'KRW'"]
 *   currency_name varchar(255) [note: "통화명, 예: 'South Korean won'"]
 *   currency_symbol varchar(255) [note: "통화기호, 예: '₩'"]
 *   tld varchar(255) [note: "최상위 도메인, 예: '.kr'"]
 *   native varchar(255) [note: "자국어 국가명, 예: '대한민국'"]
 *   region varchar(255) [note: "상위 지역명(영문), 예: 'Asia'"]
 *   region_id bigint [ref: > regions.id]
 *   subregion varchar(255) [note: "하위 지역명(영문), 예: 'Eastern Asia'"]
 *   subregion_id bigint [ref: > subregions.id]
 *   nationality varchar(255) [note: "국민명, 예: 'Korean'"]
 *   timezones text [note: "JSON 배열, 예: ['UTC+9']"]
 *   translations text [note: "다국어 JSON, 예: {'ko':'대한민국'}"]
 *   latitude numeric(10,8)
 *   longitude numeric(11,8)
 *   emoji varchar(191) [note: "국기 이모지, 예: '🇰🇷'"]
 *   "emojiU" varchar(191) [note: "유니코드, 예: 'U+1F1F0 U+1F1F7'"]
 *   created_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   flag smallint [not null, default: 1, note: "0=비활성, 1=활성"]
 *   "wikiDataId" varchar(255) [note: "위키데이터 Q번호, 예: 'Q884'"]
 * }
 * </pre>
 */
@Entity
@Immutable
@Table(name = "countries", schema = "public")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "iso3", length = 3)
    private String iso3;

    @Column(name = "numeric_code", length = 3)
    private String numericCode;

    @Column(name = "iso2", length = 2)
    private String iso2;

    @Column(name = "phonecode", length = 255)
    private String phoneCode;

    @Column(name = "capital", length = 255)
    private String capital;

    @Column(name = "currency", length = 255)
    private String currency;

    @Column(name = "currency_name", length = 255)
    private String currencyName;

    @Column(name = "currency_symbol", length = 255)
    private String currencySymbol;

    @Column(name= "tId", length = 255)
    private String tld;

    @Column(name="native", length = 255)
    private String nativeName;

    @Column(name = "region", length = 255)
    private String regionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "subregion", length = 255)
    private String subregionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subregion_id", nullable = false)
    private Subregion subregion;

    @Column(name = "nationality", length = 255)
    private String Nationality;

    @Type(JsonType.class)
    @Column(name = "timezones", columnDefinition = "text")
    private List<TimeZoneInfo> timezones;

    @Type(JsonType.class)
    @Column(name = "translations", columnDefinition = "text")
    private Map<LanguageCode, String> translations;

    @Column(name = "latitude", precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private Double longitude;

    @Column(name = "emoji", length = 191)
    private String emoji;

    @Column(name = "\"emojiU\"", length = 191)
    private String emojiU;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "flag", nullable = false, columnDefinition = "smallint default 1")
    private Short flag = 1;

    @Column(name = "\"wikiDataId\"", length = 255)
    private String wikiDataId;

    // State relationship
    @Immutable
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<State> states;

    // City relationship
    @Immutable
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<City> cities;
}
