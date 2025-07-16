package com.otterdram.otterdram.domain.user.profile;

import com.otterdram.otterdram.common.audit.superclass.timestamp.UpdatableTimestamp;
import com.otterdram.otterdram.common.enums.user.Gender;
import com.otterdram.otterdram.common.geo.address.Address;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

/** UserProfile Entity
 * <pre>
 * Table user_profiles {
 *   id bigint [pk, increment]
 *   user_id bigint [ref: - users.id, unique, not null]
 *   display_name varchar(100) [not null]
 *   bio text
 *   country_id bigint [ref: > countries.id]
 *   city_id bigint [ref: > cities.id]
 *   address varchar(255)
 *   birthday date [not null]
 *   gender Gender [not null, default: 'UNSPECIFIED']
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 * }
 * </pre>
 */

@Entity
@Table(name = "user_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends UpdatableTimestamp {

    @Id
    @SequenceGenerator(name="user_profile_seq", sequenceName = "user_profile_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Embedded
    private Address address;

    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, columnDefinition = "varchar(20) default 'UNSPECIFIED'")
    private Gender gender = Gender.UNSPECIFIED;

}
