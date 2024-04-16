package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLikeCommentKey implements Serializable {
    private Integer userId;
    private Integer movieId;
    private Integer commentId;

    public UserLikeCommentKey() {
        // 기본 생성자
    }

    public UserLikeCommentKey(Integer userId, Integer movieId, Integer commentId) {
        this.userId = userId;
        this.movieId = movieId;
        this.commentId = commentId;
    }
}
