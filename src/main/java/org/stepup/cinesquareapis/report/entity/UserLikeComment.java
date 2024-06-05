package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private Integer commentId;

    @Id
    @Column(nullable = false)
    private Integer movieId;

    @CreationTimestamp
    @Column( updatable = false)
    private LocalDateTime created;
}
