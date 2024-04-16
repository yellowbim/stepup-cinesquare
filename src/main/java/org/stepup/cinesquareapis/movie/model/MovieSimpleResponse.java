package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Getter
public class MovieSimpleResponse {
    private int movieId;

    private String title;

    private int runningTime;

    private int productionYear;

    private String nation;

    private Float score;

    public MovieSimpleResponse() {
    }

    public MovieSimpleResponse(MovieSimple movieSimple) {
        movieId = movieSimple.getMovieId();
        title = movieSimple.getTitle();
        runningTime = movieSimple.getRunningTime();
        productionYear = movieSimple.getProductionYear();
        nation = movieSimple.getNation();
        score = movieSimple.getScore();
    }
}
