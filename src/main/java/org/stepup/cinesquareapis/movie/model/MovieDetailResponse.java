package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;
import org.stepup.cinesquareapis.movie.entity.Movie;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MovieDetailResponse {
    private int movieId;

    private String movieTitle;

    private String movieTitleEn;

    private int runningTime;

    private int productionYear;

    private String nation;

    private String nations;

    private String genre;

    private String genres;

    private String director;

    private String directors;

    private String actors;

    private LocalDate openDate;

    private Float score;

    private LocalDateTime created;

    private LocalDateTime updated;

    public MovieDetailResponse() {
    }

    public MovieDetailResponse(Movie movie, MovieSimple movieSimple) {
        movieId = movieSimple.getMovieId();
        movieTitle = movieSimple.getMovieTitle();
        movieTitleEn = movie.getMovieTitleEn();
        runningTime = movieSimple.getRunningTime();
        productionYear = movieSimple.getProductionYear();
        nation = movieSimple.getNation();
        nations = movie.getNations();
        genre = movie.getGenre();
        genres = movie.getGenres();
        director = movie.getDirector();
        directors = movie.getDirectors();
        actors = movie.getActors();
        openDate = movie.getOpenDate();
        score = movieSimple.getScore();
        created = movieSimple.getCreated();
        updated = movieSimple.getUpdated();
    }
}