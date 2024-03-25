package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.report.model.UserLikeCommentRequest;
import org.stepup.cinesquareapis.report.model.UserScoreRequest;
import org.stepup.cinesquareapis.report.model.UserStatusRequest;
import org.stepup.cinesquareapis.report.service.UserReportService;

@RestController
@RequiredArgsConstructor
@Tag(name = "movie-report", description = "유저별 영화 리뷰 API")
@RequestMapping("api/users-report")
public class UserReportController {

    private final UserReportService userReportService;


    /**
     * 유저별 영화 별점 부과
     *
     * table : tb_user_movie_score
     */
    @Operation(summary = "유저별 영화 별점 부과")
    @PostMapping("score")
    public ResponseEntity<Boolean> saveScore(@RequestBody UserScoreRequest request) {
        userReportService.saveScore(request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 유저별 영화 상태 부과
     *
     * table : tb_user_movie_status
     */
    @Operation(summary = "유저별 영화 상태 부과")
    @PostMapping("status")
    public ResponseEntity<Boolean> saveScore(@RequestBody UserStatusRequest request) {
        userReportService.saveStatus(request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 유저별 코멘트 좋아요 부과
     */
    @Operation(summary = "유저별 코멘트 좋아요 부과")
    @PostMapping("like-comment")
    public ResponseEntity<Boolean> saveLikeComment(@RequestBody UserLikeCommentRequest request) {
        userReportService.saveLikeComment(request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 유저별 좋아요 한 코멘트 조회
     *
     * 코멘트 테이블, 좋아요한 코멘트 테이블 조인을 조회 필요
     */
//    @Operation(summary = "유저별 좋아요한 코멘트 조회")
//    @GetMapping("likeComment/{commentId}/{userId}")
//    public ResponseEntity<Boolean> saveLikeComment(@PathVariable("commentId") Integer commentId, @PathVariable("userId") Integer userId) {
//        userService.saveLikeComment(userId, commentId);
//        return new ResponseEntity<>(true, HttpStatus.OK);
//    }





























}
