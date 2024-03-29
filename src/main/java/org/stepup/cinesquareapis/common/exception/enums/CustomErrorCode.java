package org.stepup.cinesquareapis.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode implements ErrorCode {
    // Domain 별 Custom Error
    // Common: 10000~
    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND,10100, null),

    // User: 20000~
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,20100, null),

    // Movie: 30000~,
    NOT_FOUND_MOVIE(HttpStatus.NOT_FOUND,30100, null),
    NOT_FOUND_MOVIE_SIMPLE(HttpStatus.NOT_FOUND,30101, null),
    NOT_FOUND_MOVIE_BOXOFFICE(HttpStatus.NOT_FOUND,30200, null),
    // Report: 40000~

    // Social: 50000~,
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}