package org.stepup.cinesquareapis.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stepup.cinesquareapis.auth.jwt.RoleType;
import org.stepup.cinesquareapis.auth.model.SignUpRequest;
import org.stepup.cinesquareapis.user.model.UserUpdateRequest;

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

//    @Column
//    private Integer profile;

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

//    @Builder
//    private User(Integer userId, String account, String password, String name, String nickname, RoleType type) {
//        this.userId = userId;
//        this.account = account;
//        this.password = password;
//        this.name = name;
//        this.nickname = nickname;
//        this.type = type;
//    }


    public static User from(SignUpRequest request, PasswordEncoder encoder) {
        return User.builder()
                .account(request.account())
                .password(encoder.encode(request.password()))	// 비밀번호 암호화
                .name(request.name())
                .nickname(request.nickname())
                .type(RoleType.USER)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }

    // DB에 비밀번호를 암호화해서 저장하려면 엔티티 객체에 비밀번호가 암호화되어 있어야 함
    // User 정적 팩토리 메소드와 update()를 다음과 같이 수정한다.
//    public void update(UserUpdateRequest newMember) {
//        this.password = newMember.newPassword() == null || newMember.newPassword().isBlank() ? this.password : newMember.password();
//        this.name = newMember.name();
//    }

    public void update(UserUpdateRequest newUser, PasswordEncoder encoder) {	// 파라미터에 PasswordEncoder 추가
        this.password = newUser.newPassword() == null || newUser.newPassword().isBlank()
                ? this.password : encoder.encode(newUser.newPassword());	// 수정
        this.name = newUser.name();
        this.nickname = newUser.nickname();
    }
}