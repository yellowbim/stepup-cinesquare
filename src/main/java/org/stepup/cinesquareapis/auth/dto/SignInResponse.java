package org.stepup.cinesquareapis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.enums.RoleType;

@Getter
@Setter
public class SignInResponse {
    @Schema(description = "회원 고유 키", example = "1")
    private Integer userId;

    @Schema(description = "회원 유형", example = "USER")
    private RoleType type;

    private String accessToken;

    private String refreshToken;

    public SignInResponse(User user, String accessToken, String refreshToken) {
        this.userId = user.getUserId();
        this.type = user.getType();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}