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

    // Auth, User: 20000~
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 20100, null),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, 20101, null),
    ALREADY_REGISTED_ACCOUNT(HttpStatus.CONFLICT, 20102, null),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 20103, null),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 20104, null),

    // Movie: 30000~,
    NOT_FOUND_MOVIE(HttpStatus.NOT_FOUND,30100, null),
    NOT_FOUND_MOVIE_SIMPLE(HttpStatus.NOT_FOUND,30101, null),
    NOT_FOUND_MOVIE_BOXOFFICE(HttpStatus.NOT_FOUND,30200, null),

    // Report: 40000~
    ALREADY_REGISTED_COMMENT(HttpStatus.CONFLICT,40000, null),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND,40001, null),
    ALREADY_REGISTED_COMMENT_REPLY(HttpStatus.NOT_FOUND,40002, null),
    NOT_FOUND_COMMENT_REPLY(HttpStatus.NOT_FOUND,40003, null),
    ALREADY_REGISTED_SCORE(HttpStatus.NOT_FOUND,40004, null),
    NOT_FOUND_USER_SCORE(HttpStatus.NOT_FOUND,40005, null),
    SCORE_RANGE_NOT_VALID(HttpStatus.NOT_FOUND,40006, null),
    MOVIE_DB_UPDATE_FAILED(HttpStatus.NOT_FOUND,40007, null),
    ALERADY_REGISTED_USER_STATUS(HttpStatus.NOT_FOUND,40008, null),
    ALERADY_REGISTED_USER_LIKE_COMMENTS(HttpStatus.NOT_FOUND,40009, null),
    NOT_LIKED_COMMENT(HttpStatus.NOT_FOUND,40010, "좋아요 하지 않은 코멘트 입니다"),
    NOT_FOUND_MOVIE_SUMMARY(HttpStatus.NOT_FOUND,40011, null),

    // Social: 50000~,
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}