package org.stepup.cinesquareapis.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.time.LocalDateTime;

@Data
@Schema(description = "코멘트 답글 목록 응답 DTO")
public class CommentReplyResponse {
    @Schema(description = "코멘트 답변 고유 키", example = "1")
    private int replyId;

    @Schema(description = "코멘트 고유 키", example = "1")
    private int commentId;

    @Schema(description = "사용자 고유 키", example = "1")
    private int userId;

    @Schema(description = "코멘트 답변 내용", example = "영화 너무 재밌어요!!")
    private String content;

    @Schema(description = "코멘트 답변 좋아요 수(답변에 대한 좋아요는 구현 안됨)", example = "22")
    private int like;

    @Schema(description = "생성일", example = "2023-05-11T15:00:00")
    private LocalDateTime created;

    @Schema(description = "수정일", example = "2023-05-11T15:00:00")
    private LocalDateTime updated;

    public CommentReplyResponse(CommentReply commentReply) {
        replyId = commentReply.getReplyId();
        commentId = commentReply.getCommentId();
        userId = commentReply.getUserId();
        content = commentReply.getContent();
        like = commentReply.getLike();
        created = commentReply.getCreated();
        updated = commentReply.getUpdated();
    }
}