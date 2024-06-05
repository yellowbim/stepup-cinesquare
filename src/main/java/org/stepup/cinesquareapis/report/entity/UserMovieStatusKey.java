package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserMovieStatusKey implements Serializable {
    private Integer userId;
    private Integer movieId;

    public UserMovieStatusKey() {
        // 기본 생성자
    }

    public UserMovieStatusKey(Integer userId, Integer movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }
}
