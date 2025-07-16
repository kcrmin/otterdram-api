package com.otterdram.otterdram.domain.ugc.comment;

import com.otterdram.otterdram.common.audit.superclass.AuthorModifiable;
import com.otterdram.otterdram.common.enums.common.LanguageCode;
import com.otterdram.otterdram.common.enums.target.CommentTargetEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Entity Comment Entity
 * <pre>
 * Table entity_comments {
 *   id bigint [pk, increment]
 *   entity_type CommentTargetType [not null]
 *   entity_id bigint [not null]
 *   parent_id bigint [ref: > entity_comments.id]
 *   depth smallint [not null, default: '0', note: "카테고리 깊이 (최대 1), 0=루트, 1=1단계"]
 *   language_code LanguageCode [not null, default: 'EN']
 *   comment text [not null]
 *   created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   created_by bigint [ref: > users.id, not null]
 *   modified_at timestamp
 *   updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
 *   updated_by bigint [ref: > users.id, not null]
 *   deleted_at timestamp
 *   deleted_by bigint [ref: > users.id]
 * }
 * </pre>
 */

@Entity
@Table(name = "entity_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityComment extends AuthorModifiable {

    @Id
    @SequenceGenerator(name = "entity_comment_seq", sequenceName = "entity_comment_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_comment_seq")
    private Long id;

    @Column(name = "entity_type", nullable = false, columnDefinition = "varchar(50)")
    private CommentTargetEntity entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private EntityComment parentComment;

    @Column(name = "depth", nullable = false, columnDefinition = "smallint default 0")
    private Short depth = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10, columnDefinition = "varchar(10) default 'EN'")
    private LanguageCode languageCode = LanguageCode.EN;

    @Column(name = "comment", nullable = false, columnDefinition = "text")
    private String comment;

    // =========================== Relationships ===========================
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<EntityComment> replies = new ArrayList<>();
}
