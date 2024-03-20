package org.stepup.cinesquareapis.user.model;

import lombok.Data;
import org.stepup.cinesquareapis.user.entity.User;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private int userId;

    private String account;

    private String name;

    private String nickname;

    private LocalDateTime created;

    private LocalDateTime updated;

    public UserResponse() {
    }

    public UserResponse(User user) {
        userId = user.getUserId();
        account = user.getAccount();
        name = user.getName();
        nickname = user.getNickname();
        created = user.getCreated();
        updated = user.getUpdated();
    }
}
