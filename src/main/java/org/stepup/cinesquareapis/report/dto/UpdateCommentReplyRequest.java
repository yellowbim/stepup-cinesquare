package org.stepup.cinesquareapis.report.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentReplyRequest {
    @NotNull
    @Size(min = 1, max = 1000)
    @Pattern(regexp = "\\S.*", message = "Content must not be blank")
    private String content;
}