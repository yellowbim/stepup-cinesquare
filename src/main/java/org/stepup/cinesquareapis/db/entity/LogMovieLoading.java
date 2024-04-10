package org.stepup.cinesquareapis.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_log_movie_loading")
public class LogMovieLoading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @Column(length = 1, nullable = false)
    private Integer status;

    @Column(nullable = true)
    private Integer koficMovieCode;

    @Column(nullable = true)
    private Integer movieId;
}