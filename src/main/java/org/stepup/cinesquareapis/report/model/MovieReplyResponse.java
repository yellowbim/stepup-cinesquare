package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
public class MovieReplyResponse {
    private int replyId;
    private int userId;
    private String content;
    private int like;
    private LocalDateTime created;
    private LocalDateTime updated;

    public MovieReplyResponse(CommentReply commentReply) {
        replyId = commentReply.getReplyId();
        userId = commentReply.getUserId();
        content = commentReply.getContent();
        like = commentReply.getLike();
        created = commentReply.getCreated();
        updated = commentReply.getUpdated();
    }

}
