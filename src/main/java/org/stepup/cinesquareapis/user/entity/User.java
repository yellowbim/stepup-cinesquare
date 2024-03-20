package org.stepup.cinesquareapis.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity // JPA 엔티티 클래스임을 나타냄 (객체와 테이블 매핑)
@Getter
@Setter
@Builder
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동으로 생성
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 생성
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // JPA에서 lazy관련 에러 날 경우 사용
//@ToString
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 50, nullable = false)
    private String account;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String nickname;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;
}