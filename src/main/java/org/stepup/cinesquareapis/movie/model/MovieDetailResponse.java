package org.stepup.cinesquareapis.movie.model;

import lombok.Getter;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

import java.time.LocalDate;

@Getter
public class MovieDetailResponse {
    private int movieId;

    private String title;

    private String titleEn;

    private String thumbnail;

    private String synopsys;

    private int runningTime;

    private int productionYear;

    private String nation;

    private String[] nations;

    private String genre;

    private String[] genres;

    private String director;

    private String[] directors;

    private String[] actors;

    private String[] images;

    private LocalDate openDate;

    private Float score;

    public MovieDetailResponse() {
    }

    public MovieDetailResponse(Movie movie, MovieSimple movieSimple) {
        movieId = movieSimple.getMovieId();
        title = movieSimple.getTitle();
        titleEn = movie.getTitleEn();
        thumbnail = movie.isThumbnail() ? "https://cinesquares3.s3.ap-northeast-2.amazonaws.com/movies/" + getMovieId() + "/thumbnail.jpg" : null;
        synopsys = movie.getSynopsys();
        runningTime = movieSimple.getRunningTime();
        productionYear = movieSimple.getProductionYear();
        nation = movieSimple.getNation();
        nations = movie.getNations() != null ? movie.getNations().split(",") : new String[0];
        genre = movie.getGenre();
        genres = movie.getGenres().split(",");
        director = movie.getDirector();
        directors = movie.getDirectors() != null ? movie.getDirectors().split(",") : new String[0];
        actors = movie.getActors() != null ? movie.getActors().split(",") : new String[0];
        openDate = movie.getOpenDate();
        score = movieSimple.getScore();

        // images 배열에 URL을 붙여서 새 배열 생성
        if (movie.getImages() != null) {
            String[] x = movie.getImages().split(",");
            images = new String[x.length];  // 배열 초기화
            String baseUrl = "https://cinesquares3.s3.ap-northeast-2.amazonaws.com/movies/" + movieId + "/images/";
            for (int i = 0; i < x.length; i++) {
                images[i] = baseUrl + x[i] + ".jpg";
            }
        } else {
            images = new String[0];
        }

    }
}