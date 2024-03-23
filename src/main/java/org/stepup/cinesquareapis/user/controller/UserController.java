package org.stepup.cinesquareapis.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.user.model.CreateUserRequest;
import org.stepup.cinesquareapis.user.model.LoginUserRequest;
import org.stepup.cinesquareapis.user.model.UpdateUserRequest;
import org.stepup.cinesquareapis.user.model.UserResponse;
import org.stepup.cinesquareapis.user.service.UserService;

import java.text.ParseException;

@RequiredArgsConstructor
@Tag(name = "users", description = "회원 정보 관련 API")
@RequestMapping("api/users")
@RestController
public class UserController {

    private final UserService UserService;

    /**
     * 계정 중복 확인
     *
     * @return
     * @throws ParseException
     */
    @Operation(
        summary = "계정 중복 체크",
        description = "존재하는 계정이면 true, 존재하지 않는 계정이면 false 반환 (JSON 아님)"
    )
    @GetMapping("check-account/{account}")
    public ResponseEntity<Boolean> checkAccount(@PathVariable("account") String account) throws ParseException {
        Boolean result = UserService.checkAccount(account);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 회원가입
     *
     * @return
     * @throws ParseException
     */
    @Operation(
            summary = "회원가입",
            description = "요청 필수 값: account, password, name, nickname"
    )
    @PostMapping("")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) throws ParseException {
        UserResponse savedUser = UserService.createUser(request);

        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    /**
     * 로그인 TODO: 수정
     *
     * @return
     * @throws ParseException
     */
    @Operation(
            summary = "로그인 (단순 account, password 확인용)",
            description = "요청 필수 값: account, password, name, nickname"
    )
    @PostMapping("login")
    public ResponseEntity<Boolean> createUser(@RequestBody LoginUserRequest request) throws ParseException {
        boolean result = UserService.checkUser(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 회원 졍보 수정
     *
     * @param userId
     * @return
     * @throws ParseException
     */
    @Operation(
            summary = "회원 졍보 수정",
            description = "요청 필수 값: password, name, nickname 중 1개 이상"
    )
    @PatchMapping("{user_id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("user_id") int userId, @RequestBody UpdateUserRequest request) throws ParseException {
        UserResponse updatedUser = UserService.updateUser(userId, request);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * user_id로 회원 정보 조회
     *
     * @param userId
     * @return
     */
    @Operation(
            summary = "회원 정보 조회",
            description = "해당 유저가 존재한다는 보장이 있을 때만 사용"
    )
    @GetMapping("{user_id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("user_id") int userId) {
        UserResponse user = UserService.getUser(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}