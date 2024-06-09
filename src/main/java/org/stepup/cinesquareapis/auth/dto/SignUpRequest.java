package org.stepup.cinesquareapis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.enums.RoleType;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignUpRequest {
    @Schema(description = "계정", example = "soomin@gmail.com")
    private String account;

    @Schema(description = "비밀번호", example = "qwe123")
    private String password;

    @Schema(description = "회원 이름", example = "조수민")
    private String name;

    @Schema(description = "회원 닉네임", example = "쑴")
    private
    String nickname;

    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .account(this.account)
                .password(encoder.encode(this.password)) // 비밀번호 암호화
                .name(this.name)
                .nickname(this.nickname)
                .type(RoleType.USER) // 고정
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }
}