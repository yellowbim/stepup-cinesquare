package org.stepup.cinesquareapis.movie.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MovieReportKey implements Serializable {
    private Integer userId;
    private Integer movieId;
}
