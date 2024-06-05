package org.stepup.cinesquareapis.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.Comment;

import java.time.LocalDateTime;

@Data
@Schema(description = "코멘트 목록 응답 DTO")
public class MovieCommentResponse {
    @Schema(description = "코멘트 고유 키", example = "12")
    private Integer commentId;
    @Schema(description = "코멘트 내용", example = "영화 너무 재밌어요!!")
    private String content;
    @Schema(description = "코멘트 좋아요 수", example = "55")
    private int like;
    @Schema(description = "코멘트 답글 수", example = "11")
    private int replyCount;
    @Schema(description = "생성일", example = "2023-05-11T15:00:00")
    private LocalDateTime created;
    @Schema(description = "수정일", example = "2023-05-11T15:00:00")
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
