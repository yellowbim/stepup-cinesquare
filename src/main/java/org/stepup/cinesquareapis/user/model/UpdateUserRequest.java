package org.stepup.cinesquareapis.user.model;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String password;

    private String name;

    private String nickname;
}
