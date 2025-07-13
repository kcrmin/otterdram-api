package com.otterdram.otterdram.domain.user.social;

import com.otterdram.otterdram.common.audit.superclass.timestamp.CreatableTimestamp;
import com.otterdram.otterdram.domain.user.user.User;
import jakarta.persistence.*;

/** UserSocialAccount Entity
 * <pre>
 * Table user_social_accounts {
 *   id bigint [pk, increment]
 *   user_id bigint [not null, ref: > users.id]
 *   provider varchar(50) [not null]
 *   provider_id varchar(255) [not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   indexes {
 *     (provider, provider_id) [unique]
 *   }
 * }
 * </pre>
 */
@Entity
@Table(name = "user_social_accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
public class UserSocialAccount extends CreatableTimestamp {

    @Id
    @SequenceGenerator(name = "user_social_account_seq", sequenceName = "user_social_account_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_social_account_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;



}
