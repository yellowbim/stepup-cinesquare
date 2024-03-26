package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserLikeCommentKey.class)
@Table(name = "tb_user_like_comment")
public class UserLikeComment {

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Id
    @Column(name = "comment_id")
    private Integer commentId;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;
}
