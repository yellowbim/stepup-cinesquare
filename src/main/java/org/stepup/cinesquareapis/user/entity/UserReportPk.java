package org.stepup.cinesquareapis.user.entity;

import lombok.Data;

import java.io.Serializable;


/**
 * id, movie 복합키를 사용하기 위해서 따로
 */
@Data
public class UserReportPk implements Serializable {
    private String userId;
    private String movieId;
}
