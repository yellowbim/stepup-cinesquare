package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLikeCommentKey implements Serializable {
    private Integer userId;
    private Integer commentId;
}
