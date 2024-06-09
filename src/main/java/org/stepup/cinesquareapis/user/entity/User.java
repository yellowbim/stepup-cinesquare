package org.stepup.cinesquareapis.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stepup.cinesquareapis.auth.dto.SignUpRequest;
import org.stepup.cinesquareapis.user.enums.RoleType;

import java.time.LocalDateTime;

@Entity // JPA 엔티티 클래스임을 나타냄 (객체와 테이블 매핑)
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 파라미터가 없는 기본 생성자를 자동으로 생성
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

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column
    private String image;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType type;  // 계정 타입

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;

    public static User from(SignUpRequest request) {
        return User.builder()
                .account(request.account())
                .password(request.password())
                .name(request.name())
                .nickname(request.nickname())
                .type(RoleType.USER)
                .build();
    }

    public static User from(SignUpRequest request, PasswordEncoder encoder) {
        return User.builder()
                .account(request.account())
                .password(encoder.encode(request.password())) // 비밀번호 암호화
                .name(request.name())
                .nickname(request.nickname())
                .type(RoleType.USER)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }
}