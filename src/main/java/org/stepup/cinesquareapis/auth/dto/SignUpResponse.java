package org.stepup.cinesquareapis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.stepup.cinesquareapis.user.entity.User;

@Getter
@Setter
public class SignUpResponse {

    @Schema(description = "회원 고유키", example = "123")
    private Integer userId;

    @Schema(description = "회원 계정", example = "soomin@gmail.com")
    private String account;

    @Schema(description = "회원 이름", example = "조수민")
    private String name;

    @Schema(description = "회원 닉네임", example = "쑴")
    private String nickname;

    public SignUpResponse(User user) {
        this.userId = user.getUserId();
        this.account = user.getAccount();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }
}