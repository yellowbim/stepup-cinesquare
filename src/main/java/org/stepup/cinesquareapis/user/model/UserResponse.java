package org.stepup.cinesquareapis.user.model;

import lombok.Data;
import org.stepup.cinesquareapis.user.entity.User;

@Data
public class UserResponse {
    private int userId;

    private String account;

    private String password;

    private String name;

    private String nickname;

    public UserResponse() {
    }

    public UserResponse(User user) {
        userId = user.getUserId();
        account = user.getAccount();
        password = user.getPassword();
        name = user.getName();
        nickname = user.getNickname();
    }
}
