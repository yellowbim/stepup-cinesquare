package org.stepup.cinesquareapis.test;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stepup.cinesquareapis.common.exception.enums.CommonErrorCode;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/test")
public class TestController {
    @Operation(summary = "400 애러")
    @GetMapping("/bad-request")
    public ResponseEntity<Object> test400Error() {
        throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
    }

    @Operation(summary = "404 에러")
    @GetMapping("/user/{user_id}")
    public ResponseEntity<Object> testCustomError(@RequestParam("user_id") String userId) {
        throw new RestApiException(CustomErrorCode.NOT_FOUND_USER);
    }
}