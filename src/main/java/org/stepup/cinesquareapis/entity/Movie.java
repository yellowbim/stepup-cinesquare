package org.stepup.cinesquareapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity // JPA 엔티티 클래스임을 나타냄 (객체와 테이블 매핑)
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_movie")
public class Movie {
    @Id
    private Integer movieId;

    @Column
    private String movieTitle;

    @Column
    private Integer runningTime;

    @Column
    private Float score;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime updated;
}
