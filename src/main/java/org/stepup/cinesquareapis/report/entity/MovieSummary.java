package org.stepup.cinesquareapis.report.entity;

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
@Table(name = "tb_movie_summary")
public class MovieSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(columnDefinition = "int default 0")
    private int commentCount;

    private Float totalScore;

    @Column(columnDefinition = "int default 0")
    private int scoreCount;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_0_5;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_1;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_1_5;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_2;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_2_5;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_3;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_3_5;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_4;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_4_5;

    @Column(columnDefinition = "int default 0")
    private int scoreCount_5;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column
    private LocalDateTime updated;
}
