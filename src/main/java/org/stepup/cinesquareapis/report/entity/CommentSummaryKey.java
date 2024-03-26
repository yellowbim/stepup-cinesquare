package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentSummaryKey implements Serializable {
    private Integer commentId;
    private Integer movieId;
    private Integer userId;
}
