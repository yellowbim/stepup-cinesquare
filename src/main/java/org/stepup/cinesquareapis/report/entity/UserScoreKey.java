package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserScoreKey implements Serializable {
    private Integer userId;
    private Integer movieId;

    public UserScoreKey() {
        // 기본 생성자
    }

    public UserScoreKey(Integer userId, Integer movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }
}
