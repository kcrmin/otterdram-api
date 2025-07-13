package com.otterdram.otterdram.domain.user.user;

import com.otterdram.otterdram.common.audit.timestamp.SoftDeletableTimestamp;
import com.otterdram.otterdram.common.enums.Privacy;
import com.otterdram.otterdram.common.enums.UserStatus;
import com.otterdram.otterdram.domain.user.block.UserBlock;
import com.otterdram.otterdram.domain.user.follow.UserFollow;
import com.otterdram.otterdram.domain.user.role.UserRole;
import com.otterdram.otterdram.domain.user.profile.UserProfile;
import com.otterdram.otterdram.domain.user.setting.UserSetting;
import com.otterdram.otterdram.domain.user.social.UserSocialAccount;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** User Entity
 * <pre>
 * Table users {
 *   id id [pk, increment]
 *   username varchar(50) [not null, unique, note: "@username"]
 *   email varchar(255) [not null, unique, note: "인증된 이메일만"]
 *   profile_image varchar(255)
 *   verified boolean [not null, default: false, note: "인증된 인플루언서/전문가"]
 *   user_privacy Privacy [not null, default: 'PUBLIC']
 *   user_status UserStatus [not null, default: 'ACTIVE']
 *   last_login_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   deleted_at timestamp
 * }
 * </pre>
 */

@Entity
@Table(name = "users")
public class User extends SoftDeletableTimestamp {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "verified", nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_privacy", nullable = false, columnDefinition = "varchar(20) default 'PUBLIC'")
    private Privacy userPrivacy = Privacy.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name = "last_login_at", nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private Instant lastLoginAt = Instant.now();

    // UserProfile relationship
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserProfile userProfile;

    // UserSetting relationship
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserSetting userSetting;

    // UserSocialAccount relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSocialAccount> userSocialAccounts = new ArrayList<>();

    // UserRole relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>();

    // UserFollow relationship
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserFollow> userFollows = new ArrayList<>();

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserFollow> userFollowees = new ArrayList<>();

    // UserBlock relationship
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserBlock> userBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserBlock> userBlockedBy = new ArrayList<>();

}
