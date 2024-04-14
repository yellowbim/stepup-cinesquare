package org.stepup.cinesquareapis.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_log_movie_loading")
public class LogMovieLoading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    // 0: 정상, 1: 유효성 검사에 의한 제외, 2: API 오류, 9: 알 수 없는 오류
    @Column( nullable = false)
    private short status;

    @Column(nullable = true)
    private String koficMovieCode;

    @Column(nullable = true)
    private Integer movieId;

    private String message;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;
}