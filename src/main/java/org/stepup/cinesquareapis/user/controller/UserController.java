package org.stepup.cinesquareapis.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stepup.cinesquareapis.common.annotation.UserAuthorize;
import org.stepup.cinesquareapis.common.dto.DataResponse;
import org.stepup.cinesquareapis.common.dto.ResultResponse;
import org.stepup.cinesquareapis.user.dto.UpdateUserRequest;
import org.stepup.cinesquareapis.user.dto.UserResponse;
import org.stepup.cinesquareapis.user.service.UserService;

@RequiredArgsConstructor
@Tag(name = "2 users", description = "유저 API")
@RequestMapping("api/users")
@RestController
public class UserController {

    private final UserService userService;

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
        boolean result = userService.checkAccount(account);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(result);

        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 조회
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "내 정보 조회")
    @GetMapping("me")
    @UserAuthorize
    public ResponseEntity<DataResponse<UserResponse>> getUser(@AuthenticationPrincipal User principal) {
        Integer userId = Integer.parseInt(principal.getUsername());

        UserResponse data = userService.getUser(userId);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 수정
     *
     * @return return ResponseEntity.ok(response)
     */
    @Operation(
            summary = "내 정보 수정 ",
            description = "요청 필수 값: password, name, nickname 중 1개 이상"
    )
    @PatchMapping("me")
    @UserAuthorize
    public ResponseEntity<DataResponse<UserResponse>> updateUser(@AuthenticationPrincipal User principal, @RequestBody UpdateUserRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        UserResponse data = userService.updateUser(userId, request);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("me")
    public void deleteMember(@AuthenticationPrincipal User principal) {
        Integer userId = Integer.parseInt(principal.getUsername());
        userService.deleteUser(userId);
    }

    /**
     * 프로필 이미지 업로드
     *
     * @return return ResponseEntity.ok(response)
     */
    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(value =  "me/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse<UserResponse>> profileUpload(@AuthenticationPrincipal User principal,
             @RequestPart("file") MultipartFile multipartFile) throws Exception {
        Integer userId = Integer.parseInt(principal.getUsername());
        UserResponse data = userService.profileUpload(multipartFile, userId);
        DataResponse<UserResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}