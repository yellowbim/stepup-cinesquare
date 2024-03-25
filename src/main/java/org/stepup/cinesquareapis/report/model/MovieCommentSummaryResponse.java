package org.stepup.cinesquareapis.report.model;

import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentSummary;

@Data
public class MovieCommentSummaryResponse {
    private int commentId;
    private int movieId;
    private int userId;
    private String content;
    private int score;
    private String nickname;
    private int like;
    private int replyCount;

    public MovieCommentSummaryResponse(CommentSummary commentSummary) {
        commentId = commentSummary.getCommentId();
        movieId = commentSummary.getMovieId();
        userId = commentSummary.getUserId();
        content = commentSummary.getContent();
        score = commentSummary.getScore();
        nickname = commentSummary.getNickname();
        like = commentSummary.getLike();
        replyCount = commentSummary.getReplyCount();
    }

}
