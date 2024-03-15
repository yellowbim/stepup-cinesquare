package org.stepup.cinesquareapis.user.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@IdClass(UserReportPk.class)
@Table(name = "tb_user_movie_report")
public class UserReport {

    @Id
    @Column(nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;

    @Id
    @Column(nullable = false)
    private String movieId;

    @ColumnDefault("0")
    @Column(name = "status")
    private Integer status;

    @ColumnDefault("0")
    @Column(name = "score")
    private Integer score;

    @ColumnDefault("0")
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

}

