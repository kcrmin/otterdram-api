package com.otterdram.otterdram.domain.user.setting;

import com.otterdram.otterdram.common.audit.superclass.timestamp.UpdatableTimestamp;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** UserSetting Entity
 * <pre>
 * Table user_settings {
 *   id bigint [pk, increment]
 *   user_id bigint [ref: - users.id, not null, unique]
 *   terms_version_agreed varchar(50) [not null, note: "약관버전. 예: '2024.06'"]
 *   lang_preference LanguageCode [not null, default: 'en']
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 * }
 * </pre>
 */

@Entity
@Table(name = "user_settings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSetting extends UpdatableTimestamp {

    @Id
    @SequenceGenerator(name = "user_setting_seq", sequenceName = "user_setting_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_setting_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "terms_version_agreed", nullable = false, length = 50)
    private String termsVersionAgreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang_preference", nullable = false, columnDefinition = "varchar(10) default 'EN'")
    private LanguageCode langPreference = LanguageCode.EN;
}
