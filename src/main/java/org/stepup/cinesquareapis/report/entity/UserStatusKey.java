package org.stepup.cinesquareapis.report.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStatusKey implements Serializable {
    private Integer userId;
    private Integer movieId;
}
