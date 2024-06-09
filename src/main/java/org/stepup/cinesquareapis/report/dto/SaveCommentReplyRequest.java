package org.stepup.cinesquareapis.report.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.stepup.cinesquareapis.report.entity.CommentReply;

@Data
public class SaveCommentReplyRequest {
    @NotNull
    @Size(min = 1, max = 1000)
    @Pattern(regexp = "\\S.*", message = "Content must not be blank")
    private String content;

    public CommentReply toEntity(Integer commentId, Integer userId) {
        return new CommentReply().builder()
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .build();
    }
}