package org.stepup.cinesquareapis.movie.dto;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Getter
public class MovieRankResponse {
    private MovieSimpleResponse movie;

    private int rank;

    public MovieRankResponse() {
    }

    public MovieRankResponse(MovieSimple movie, int rank) {
        this.movie = new MovieSimpleResponse(movie);
        this.rank = rank;
    }
}