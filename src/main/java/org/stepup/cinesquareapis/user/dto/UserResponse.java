package org.stepup.cinesquareapis.user.dto;

import lombok.Data;
import org.stepup.cinesquareapis.user.entity.User;

@Data
public class UserResponse {
    private int userId;

    private String account;

    private String name;

    private String nickname;

    private String image;

    public UserResponse() {
    }

    public UserResponse(User user) {
        userId = user.getUserId();
        account = user.getAccount();
        name = user.getName();
        nickname = user.getNickname();
        image = user.getImage() != null ? "https://cinesquares3.s3.ap-northeast-2.amazonaws.com/" + user.getImage() : null;
    }
}
