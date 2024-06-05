package org.stepup.cinesquareapis.movie.dto;

import lombok.Data;
import org.stepup.cinesquareapis.movie.entity.Movie;

@Data
public class RandomMovieResponse {
    private Integer movieId;
    private String title;
    private String thumbnail;
    private short productionYear;
    private String nation;

    public RandomMovieResponse(Movie randomMovieResponse) {
        movieId = randomMovieResponse.getMovieId();
        title = randomMovieResponse.getTitle();
        thumbnail = randomMovieResponse.isThumbnail() ? "https://cinesquares3.s3.ap-northeast-2.amazonaws.com/movies/" + getMovieId() + "/thumbnail.jpg" : null;
        productionYear = randomMovieResponse.getProductionYear();
        nation = randomMovieResponse.getNation();
    }
}
