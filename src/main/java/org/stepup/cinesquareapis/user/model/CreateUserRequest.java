package org.stepup.cinesquareapis.user.model;

import lombok.Data;
import org.stepup.cinesquareapis.user.entity.User;

@Data
public class CreateUserRequest {
    private String account;

    private String password;

    private String name;

    private String nickname;

    public User toEntity() {
        return User.builder()
                .account(account)
                .password(password)
                .name(name)
                .nickname(nickname)
                .build();
    }
}
