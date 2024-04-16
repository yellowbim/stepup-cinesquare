package org.stepup.cinesquareapis.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@IdClass(UserStatusKey.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_user_movie_status")
public class UserStatus {
    @Id
    @Column(nullable = false)
    private Integer userId;

    @Id
    @Column(nullable = false)
    private Integer movieId;

    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

}
