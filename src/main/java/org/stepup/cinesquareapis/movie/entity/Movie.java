package org.stepup.cinesquareapis.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 설정
    private Integer movieId;

    @Column(length = 50, nullable = false)
    private String movieTitle;

    @Column(length = 3)
    private Integer runningTime;

    @Column(length = 4)
    private Integer productionYear;

    @Column(length = 20)
    private String nation;

    @Column
    private Float score;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column
    private LocalDateTime updated;
}
