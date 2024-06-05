package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable // 데이터베이스에서 만들었기 때문에 수정되지 않을 예정 (view)
@IdClass(MovieCommentSummaryKey.class)
@Table(name="v_movie_comment_summary")
public class MovieCommentSummary {
    @Id
    @Column(nullable = false)
    private Integer commentId;

    @Id
    @Column(nullable = false)
    private Integer movieId;

    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(length = 1000)
    private String content;

    @ColumnDefault("0")
    private Float score;

    private String nickname;

    @ColumnDefault("0")
    @Column(name = "like") // 예약어라 제외를 시켜주는 방법
    private Integer like;

    @ColumnDefault("0")
    private Integer replyCount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

//    @UpdateTimestamp
//    @Column(name = "updated")
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime updated;



//    CREATE OR REPLACE
//            ALGORITHM = UNDEFINED VIEW `cinesquare`.`v_movie_comment_summary` AS
//            select
//    A.movie_id ,
//    A.comment_id,
//    A.user_id ,
//    A.content ,
//    IFNULL(C.score , 0) as score,
//    B.nickname ,
//    IFNULL(COUNT(D.comment_id), 0) as `like` ,
//    IFNULL(COUNT(E.reply_id), 0) as reply_count ,
//    A.created
//    from cinesquare.tb_movie_comment A
//    LEFT JOIN cinesquare.tb_user B
//    on A.user_id = B.user_id
//    LEFT join cinesquare.tb_user_movie_score C
//    on A.user_id = C.user_id
//    AND A.movie_id  = C.movie_id
//    LEFT join cinesquare.tb_user_like_comment D
//    ON A.comment_id = D.comment_id
//    LEFT join cinesquare.tb_movie_comment_reply E
//    ON A.comment_id = E.comment_id
//    group by A.user_id, A.movie_id
}
