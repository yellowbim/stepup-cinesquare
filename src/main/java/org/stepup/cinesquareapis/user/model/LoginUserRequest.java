package org.stepup.cinesquareapis.user.model;

import lombok.Data;
import org.stepup.cinesquareapis.user.entity.User;

@Data
public class LoginUserRequest {
    private String account;

    private String password;

    public User toEntity() {
        return User.builder()
                .account(account)
                .password(password)
                .build();
    }
}
