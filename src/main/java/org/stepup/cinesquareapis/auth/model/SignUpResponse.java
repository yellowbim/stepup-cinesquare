package org.stepup.cinesquareapis.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.stepup.cinesquareapis.user.entity.User;

public record SignUpResponse(
        @Schema(description = "회원 고유키", example = "123")
        Integer userId,
        @Schema(description = "회원 아이디", example = "colabear754")
        String account,
        @Schema(description = "회원 이름", example = "콜라곰")
        String name,
        @Schema(description = "회원 닉네임", example = "콜라곰")
        String nickname
) {
    public static SignUpResponse from(User member) {
        return new SignUpResponse(
                member.getUserId(),
                member.getAccount(),
                member.getName(),
                member.getNickname()
        );
    }
}
