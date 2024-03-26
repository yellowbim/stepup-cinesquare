package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable // 데이터베이스에서 만들었기 때문에 수정되지 않을 예정 (view)
@IdClass(CommentSummaryKey.class)
@Table(name="v_movie_comment_summary")
public class CommentSummary {

    @Id
    @Column(nullable = false)
    private Integer commentId;

    @Id
    @Column(nullable = false)
    private Integer movieId;

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(name = "content", length = 1000)
    private String content;

    @ColumnDefault("0")
    @Column(name = "score")
    private Double score;

    @Column(name = "nickname")
    private String nickname;

    @ColumnDefault("0")
    @Column(name = "like") // 예약어라 제외를 시켜주는 방법
    private Integer like;

    @ColumnDefault("0")
    @Column(name = "reply_count")
    private Integer replyCount;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

//    select
//    A.comment_id,
//    A.movie_id ,
//    A.user_id ,
//    A.content ,
//    IFNULL(C.score, 0),
//    B.nickname ,
//    IFNULL(A.`like`, 0) ,
//    IFNULL(A.reply_count, 0) ,
//    A.created ,
//    A.updated
//    from cinesquare.tb_movie_comment A
//    LEFT JOIN cinesquare.tb_user B
//    on A.user_id = B.user_id
//    LEFT join cinesquare.tb_user_movie_score C
//    on B.user_id = C.user_id
}
