package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStatusKey implements Serializable {
    private Integer userId;
    private Integer movieId;

    public UserStatusKey() {
        // 기본 생성자
    }

    public UserStatusKey(Integer userId, Integer movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }
}
