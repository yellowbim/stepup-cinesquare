package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;

@Getter
public class MovieRankResponse {
    private int movieId;

    private int rank;

    public MovieRankResponse() {
    }

    public MovieRankResponse(int movieId, int rank) {
        this.movieId = movieId;
        this.rank = rank;
    }

    public MovieRankResponse(MovieBoxoffice movieBoxoffice) {
        movieId = movieBoxoffice.getMovieId();
        rank = movieBoxoffice.getRank();
    }
}