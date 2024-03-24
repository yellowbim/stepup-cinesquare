package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MovieAllResponse {
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

    public MovieAllResponse() {
    }

    public MovieAllResponse(Movie movie, MovieDetail movieDetail) {
        movieId = movie.getMovieId();
        movieTitle = movie.getMovieTitle();
        movieTitleEn = movieDetail.getMovieTitleEn();
        runningTime = movie.getRunningTime();
        productionYear = movie.getProductionYear();
        nation = movie.getNation();
        nations = movieDetail.getNations();
        genre = movieDetail.getGenre();
        genres = movieDetail.getGenres();
        director = movieDetail.getDirector();
        directors = movieDetail.getDirectors();
        actors = movieDetail.getActors();
        openDate = movieDetail.getOpenDate();
        score = movie.getScore();
        created = movie.getCreated();
        updated = movie.getUpdated();
    }
}
