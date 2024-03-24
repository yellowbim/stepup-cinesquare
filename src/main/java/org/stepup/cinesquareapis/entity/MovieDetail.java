package org.stepup.cinesquareapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity // JPA 엔티티 클래스임을 나타냄 (객체와 테이블 매핑)
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tb_movie_detail")
public class MovieDetail {
    @Id
    private Integer movieId;

    @Column
    private Integer source;

    @Column
    private Integer koficMovieCode;

    @Column
    private String movieTitle;

    @Column
    private String movieTitleEn;

    @Column
    private String genre;

    @Column
    private String genreList;

    @Column
    private Integer productionYear;

    @Column
    private Date openDate;

    @Column
    private String nation;

    @Column
    private String directorList;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime updated;
}
