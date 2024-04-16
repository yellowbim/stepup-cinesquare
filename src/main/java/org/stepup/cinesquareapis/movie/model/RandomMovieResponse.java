package org.stepup.cinesquareapis.movie.model;

import lombok.Data;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Data
public class RandomMovieResponse {
    private Integer movieId;
    private String title;
    private short productionYear;
    private String nation;

    public RandomMovieResponse(MovieSimple randomMovieResponse) {
        movieId = randomMovieResponse.getMovieId();
        title = randomMovieResponse.getTitle();
        productionYear = randomMovieResponse.getProductionYear();
        nation = randomMovieResponse.getNation();
    }
}
