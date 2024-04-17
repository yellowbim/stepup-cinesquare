package org.stepup.cinesquareapis.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.model.DataResponse;
import org.stepup.cinesquareapis.common.model.ResultResponse;
import org.stepup.cinesquareapis.user.model.CreateUserRequest;
import org.stepup.cinesquareapis.user.model.LoginUserRequest;
import org.stepup.cinesquareapis.user.model.UpdateUserRequest;
import org.stepup.cinesquareapis.user.model.UserResponse;
import org.stepup.cinesquareapis.user.service.UserService;

@RequiredArgsConstructor
@Tag(name = "2 users", description = "유저 API")
@RequestMapping("api/users")
@RestController
@PreAuthorize("hasAuthority('USER')")
public class UserController {

    private final UserService UserService;

    /**
     * 계정 존재 여부 확인
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(
        summary = "계정 중복 체크",
        description = "존재하는 계정이면 true, 존재하지 않는 계정이면 false 반환"
    )
    @GetMapping("check-account/{account}")
    public ResponseEntity<ResultResponse<Boolean>> checkAccount(@PathVariable("account") String account) {
        boolean result = UserService.checkAccount(account);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(result);

        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(
            summary = "회원가입",
            description = "요청 필수 값: account, password, name, nickname"
    )
    @PostMapping("")
    public ResponseEntity<DataResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse data = UserService.createUser(request);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 TODO: 수정
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(
            summary = "로그인 (단순 account, password 확인용)",
            description = "성공하면 true, 실패하면 false 반환"
    )
    @PostMapping("login")
    public ResponseEntity<ResultResponse<Boolean>> createUser(@RequestBody LoginUserRequest request) {
        boolean result = UserService.checkUser(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(result);

        return ResponseEntity.ok(response);
    }

    /**
     * 회원 졍보 수정
     *
     * @param userId
     * @return return ResponseEntity.ok(response);
     */
    @Operation(
        summary = "회원 졍보 수정 ",
        description = "요청 필수 값: password, name, nickname 중 1개 이상"
    )
    @PatchMapping("")
    public ResponseEntity<DataResponse<UserResponse>> updateUser(@RequestParam("user_id") int userId, @RequestBody UpdateUserRequest request) {
        UserResponse data = UserService.updateUser(userId, request);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * user_id로 회원 정보 조회
     *
     * @param userId
     * @return
     */
    @Operation(
            summary = "회원 정보 조회 (추후 내정보 조회와 타인 정보 조회로 분리될 예정)",
            description = "해당 유저가 존재한다는 보장이 있을 때만 사용"
    )
    @GetMapping("{user_id}")
    public ResponseEntity<DataResponse<UserResponse>> getUser(@PathVariable("user_id") int userId) {
        UserResponse data = UserService.getUser(userId);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}