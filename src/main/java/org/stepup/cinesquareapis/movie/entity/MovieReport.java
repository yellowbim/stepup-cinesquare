package org.stepup.cinesquareapis.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Service
@IdClass(MovieReportKey.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_user_movie_report")
public class MovieReport {
    @Id
    @Column(nullable = false)
    private Integer userId;

    @Id
    @Column(nullable = false)
    private Integer movieId;

    @ColumnDefault("0")
    @Column(name = "status")
    private int status;

    @ColumnDefault("0")
    @Column(name = "score")
    private int score;

//    @ColumnDefault("")
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

}
