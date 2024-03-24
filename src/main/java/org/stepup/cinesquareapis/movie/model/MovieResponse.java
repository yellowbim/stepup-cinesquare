package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.Movie;

import java.time.LocalDateTime;

@Getter
public class MovieResponse {
    private int movieId;

    private String movieTitle;

    private int runningTime;

    private int productionYear;

    private String nation;

    private Float score;

    private LocalDateTime created;

    private LocalDateTime updated;

    public MovieResponse() {
    }

    public MovieResponse(Movie movie) {
        movieId = movie.getMovieId();
        movieTitle = movie.getMovieTitle();
        runningTime = movie.getRunningTime();
        productionYear = movie.getProductionYear();
        nation = movie.getNation();
//        score = movie.getScore() != null ? movie.getScore() : null;
        created = movie.getCreated();
        updated = movie.getUpdated();
    }
}
