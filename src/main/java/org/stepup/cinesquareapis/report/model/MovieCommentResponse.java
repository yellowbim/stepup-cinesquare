package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

import java.time.LocalDateTime;

@Data
public class MovieCommentResponse {
    private Integer commentId;
    private String content;
    private int like;
    private int replyCount;
    private LocalDateTime created;
    private LocalDateTime updated;

    // Comment 객체를 MovieCommentResponse로 변환하는 생성자
    public MovieCommentResponse(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.like = comment.getLike();
        this.replyCount = comment.getReplyCount();
        this.created = comment.getUpdated();
        this.updated = comment.getUpdated();
    }
}
