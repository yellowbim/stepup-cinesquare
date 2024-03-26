package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.model.ListResponse;
import org.stepup.cinesquareapis.common.model.ResultResponse;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;
import org.stepup.cinesquareapis.report.model.UserLikeCommentRequest;
import org.stepup.cinesquareapis.report.model.UserScoreRequest;
import org.stepup.cinesquareapis.report.model.UserStatusRequest;
import org.stepup.cinesquareapis.report.service.UserReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "users-report", description = "유저별 영화 리뷰 API")
@RequestMapping("api/user-reports")
public class UserReportController {

    private final UserReportService userReportService;


    /**
     * 유저별 영화 별점 부과
     *
     * table : tb_user_movie_score
     */
    @Operation(summary = "유저별 영화 별점 부과")
    @PostMapping("score")
    public ResponseEntity<ResultResponse<Boolean>> saveScore(@RequestBody UserScoreRequest request) {
        Boolean data = userReportService.saveScore(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 영화 상태 부과
     *
     * table : tb_user_movie_status
     */
    @Operation(summary = "유저별 영화 상태 부과")
    @PostMapping("status")
    public ResponseEntity<ResultResponse<Boolean>> saveScore(@RequestBody UserStatusRequest request) {
        Boolean data = userReportService.saveStatus(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 코멘트 좋아요 부과
     */
    @Operation(summary = "유저별 코멘트 좋아요 부과")
    @PostMapping("like-comment")
    public ResponseEntity<ResultResponse<Boolean>> saveLikeComment(@RequestBody UserLikeCommentRequest request) {
        Boolean data = userReportService.saveLikeComment(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 영화별 별점 조회
     *
     */
    @Operation(summary = "유저별 영화별 별점 조회")
    @GetMapping("score/movies/{movie_id}/users/{user_id}")
    public ResponseEntity<ResultResponse<Double>> searchMovieUserScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        Double data = userReportService.searchMovieUserScore(userId, movieId);
        ResultResponse<Double> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 영화별 상태 조회
     *
     */
    @Operation(summary = "유저별 영화별 상태 조회")
    @GetMapping("status/movies/{movie_id}/users/{user_id}")
    public ResponseEntity<ResultResponse<Integer>> searchMovieUserStatus(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        int data = userReportService.searchMovieUserStatus(userId, movieId);
        ResultResponse<Integer> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 좋아요한 코멘트 목록 조회
     *
     */
    @Operation(summary = "유저별 좋아요한 코멘트 목록 조회")
    @GetMapping("like-comments/users/{user_id}")
    public ResponseEntity<ListResponse<List<Integer>>> searchUserLikeCommentList(@PathVariable("user_id") Integer userId) {
        List<Integer> data = userReportService.searchUserLikeCommentList(userId);
        ListResponse<List<Integer>> response = new ListResponse<>();
        response.setList(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }




























}
