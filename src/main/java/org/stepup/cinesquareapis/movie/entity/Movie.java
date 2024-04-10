package org.stepup.cinesquareapis.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(length = 1)
    private short source;

    @Column(length = 10)
    private String koficMovieCode;

    @Column(length = 50)
    private String movieTitle;

    @Column(length = 100)
    private String movieTitleEn;

    @Column(nullable = false)
    private boolean thumbnail;

    private short runningTime;

    @Column(length = 20)
    private String genre;

    @Column(length = 50)
    private String genres;

    private short productionYear;

    @Column
    private LocalDate openDate;

    @Column(length = 20)
    private String nation;

    @Column(length = 50)
    private String nations;

    @Column(length = 20)
    private String director;

    @Column(length = 100)
    private String directors;

    @Column(length = 300)
    private String actors;

    @Column
    private String posterIds;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column
    private LocalDateTime updated;
}
