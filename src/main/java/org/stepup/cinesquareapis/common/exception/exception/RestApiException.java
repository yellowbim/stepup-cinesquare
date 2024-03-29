package org.stepup.cinesquareapis.common.exception.exception;

import lombok.Getter;
import org.stepup.cinesquareapis.common.exception.enums.ErrorCode;

//@Getter
//@RequiredArgsConstructor
//public class RestApiException extends RuntimeException {
//    private final ErrorCode errorCode;
//
//    private final String customMessage;
//}

@Getter
public class RestApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;

    // customMessage를 사용하는 생성자
    public RestApiException(ErrorCode errorCode, String customMessage) {
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    // customMessage를 사용하지 않는 생성자
    public RestApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.customMessage = null; // or you can provide a default value
    }
}