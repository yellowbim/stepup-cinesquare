package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.model.DataResponse;
import org.stepup.cinesquareapis.common.model.ListResponse;
import org.stepup.cinesquareapis.common.model.ResultResponse;
import org.stepup.cinesquareapis.report.model.*;
import org.stepup.cinesquareapis.report.service.UserReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "4 user-reports", description = "유저별 영화 리뷰 API")
@RequestMapping("api/user-reports")
public class UserReportController {

    private final UserReportService userReportService;

    /**
     * 유저별 영화별 별점 조회
     *
     */
    @Operation(summary = "유저별 영화별 별점 조회",
                description = "- 별점을 한번도 주지 않은 경우 : return 0 => 변경 시 저장 API 요청 필요<br>" +
                                "- 별점이 0이 아닌 경우 : return 0.5~5 => 수정 시 수정 API, 0으로 변경 시 삭제 API 요청 필요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "조회에 실패하는경우 HTTP 상태코드", content = @Content())
    })
    @GetMapping("movies/{movie_id}/users/{user_id}/score")
    public ResponseEntity<ResultResponse<Double>> searchMovieUserScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        Double data = userReportService.searchMovieUserScore(userId, movieId);
        ResultResponse<Double> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 영화 별점 부과
     *
     * table : tb_user_movie_score
     */
    @Operation(summary = "영화별 사용자 별점 등록",
                description = "기본 Flow : 등록 범위 0.5~1, 값이 있는 경우 수정 API 요청, 0으로 초기화 하는 경우 삭제 API 요청<br>" +
                        "요청 필수값 : score (필수로 안보내면 어떤 0으로 셋팅되기 때문에 수정이랑 헷갈릴 수 있음)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
        @ApiResponse(responseCode = "500", description = "저장에 실패하는경우 HTTP 상태코드", content = @Content()),
        @ApiResponse(responseCode = "40004", description = "이미 존재하는 경우 에러코드 (수정 API로 요청)", content = @Content()),
        @ApiResponse(responseCode = "40006", description = "score 범위가 허용하지 않는 값인 경우", content = @Content()),
        @ApiResponse(responseCode = "40007", description = "영화 테이블 score 업데이트 실패 (기존 데이터가 없는 경우)", content = @Content())
    })
    @PostMapping("movies/{movie_id}/users/{user_id}/score")
    public ResponseEntity<HttpStatus> saveScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId, @RequestBody UserScoreRequest request) {
        Boolean data = userReportService.saveScore(userId, movieId, request);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 유저별 영화 별점 수정
     *
     * table : tb_user_movie_score
     */
    @Operation(summary = "영화별 사용자 별점 수정",
            description = "요청 필수값 : score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "500", description = "수정에 실패하는경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40005", description = "존재하지 않는 경우 (저장 API로 요청)", content = @Content()),
            @ApiResponse(responseCode = "40007", description = "영화 테이블 score 업데이트 실패 (기존 데이터가 없는 경우)", content = @Content())
    })
    @PatchMapping("movies/{movie_id}/users/{user_id}/score")
    public ResponseEntity<HttpStatus> updateScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId, @RequestBody UserScoreRequest request) {
        Boolean data = userReportService.updateScore(userId, movieId, request);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 유저별 영화 별점 삭제
     *
     * table : tb_user_movie_score
     */
    @Operation(summary = "영화별 사용자 별점 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 삭제 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @DeleteMapping("movies/{movie_id}/users/{user_id}/score")
    public ResponseEntity<HttpStatus> deleteScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        userReportService.deleteScore(userId, movieId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 유저별 영화별 상태 조회
     *
     */
    @Operation(summary = "영화별 사용자 상태 조회",
                description = "- 상태가 변경되어있는 경우(보고싶어요 상태) return : true<br>" +
                              "- 상태가 변경되어있지 않은 경우(아무 상태 아닌경우) return : false")
    @GetMapping("movies/{movie_id}/users/{user_id}/status")
    public ResponseEntity<ResultResponse<Boolean>> getMovieUserStatus(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        Boolean data = userReportService.getMovieUserStatus(userId, movieId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 영화 상태 저장
     *
     * table : tb_user_movie_status
     */
    @Operation(summary = "영화별 사용자 상태 저장",
                description = "요청 필수값 : user_id, movie_id<br>" +
                              "기본 Flow : 해당 테이블에 데이터가 있으면 '보고싶어요' 상태<br>"+
                              "존재하지 않으면 아무 상태도 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "500", description = "저장에 실패하였을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40008", description = "이미 등록된 상태의 경우 에러코드 (삭제 API 요청)", content = @Content())
    })
    @PostMapping("movies/{movie_id}/users/{user_id}/status")
    public ResponseEntity<HttpStatus> saveScore(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        Boolean data = userReportService.saveStatus(userId, movieId);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 유저별 영화 상태 삭제
     *
     * table : tb_user_movie_status
     */
    @Operation(summary = "영화별 사용자 상태 삭제",
                description = "요청 필수값 : user_id, movie_id<br>" +
                              "기본 Flow : 해당 테이블에 데이터가 있으면 '보고싶어요' 상태<br>"+
                              "존재하지 않으면 아무 상태도 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @DeleteMapping("movies/{movie_id}/users/{user_id}/status")
    public ResponseEntity<HttpStatus> deleteStatus(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        Boolean data = userReportService.deleteStatus(userId, movieId);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 유저별 좋아요한 코멘트 목록 조회
     *
     */
    @Operation(summary = "유저별 좋아요한 코멘트 목록 조회",
                description = "user_id, movie_id 기준으로 comment_id 목록 조회" +
                        "return 값 : comment_id 리스트, 해당 목록을 가지고 코멘트 목록에서 좋아요 했는지 비교해서 판단해야됨!!")
    @GetMapping("movies/{movie_id}/users/{user_id}/like-comments")
    public ResponseEntity<ListResponse<List<MovieLikeCommentResponse>>> getUserLikeCommentList(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId) {
        List<MovieLikeCommentResponse> data = userReportService.getUserLikeCommentList(userId, movieId);
        ListResponse<List<MovieLikeCommentResponse>> response = new ListResponse<>();
        response.setList(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 코멘트 좋아요 등록
     */
    @Operation(summary = "유저별 코멘트 좋아요 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "500", description = "저장에 실패하였을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content()),
            @ApiResponse(responseCode = "40009", description = "이미 등록된 상태의 경우 에러코드 (삭제 API 요청)", content = @Content())
    })
    @PostMapping("movies/{movie_id}/users/{user_id}/like-comments/{comment_id}")
    public ResponseEntity<ResultResponse<Boolean>> saveLikeComment(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Boolean data = userReportService.saveLikeComment(userId, movieId, commentId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /**
     * 유저별 코멘트 좋아요 삭제
     */
    @Operation(summary = "유저별 코멘트 좋아요 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @DeleteMapping("movies/{movie_id}/users/{user_id}/like-comments/{comment_id}")
    public ResponseEntity<ResultResponse<Boolean>> deleteLikeComment(@PathVariable("user_id") Integer userId, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Boolean data = userReportService.deleteLikeComment(userId, movieId, commentId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 평가한 별점 개수 조회
     *
     * @param user_id
     * table : tv_user_movie_score
     */
    @Operation(summary = "평가한 별점 개수 조회",
            description = "https://pedia.watcha.com/ko-KR/review 평가하기 영화 별점 개수 조회")
    @GetMapping("users/{user_id}/score-counts")
    public ResponseEntity<DataResponse<Integer>> getScoredCount(@PathVariable("user_id") Integer userId) {
        int data = userReportService.getScoredCount(userId);
        DataResponse<Integer> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 좋아요 한 코맨트 개수 조회
     *
     * @param user_id
     * table : tv_user_movie_score
     */
    @Operation(summary = "좋아요 한 코맨트 개수 조회")
    @GetMapping("users/{user_id}/like-comment-counts")
    public ResponseEntity<DataResponse<Integer>> getLikeCommentCounts(@PathVariable("user_id") Integer userId) {
        int data = userReportService.getLikeCommentCounts(userId);
        DataResponse<Integer> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 평가한 영화 목록 조회(별점만)
     *
     * @param user_id
     * table : tv_user_movie_score
     */
//    @Operation(summary = "평가한 영화 목록 조회(별점 평가만)")
//    @GetMapping("movies/scored")
//    public ResponseEntity<ListResponse<List<UserScoredResponse>>> getScoredMovies(@RequestParam("user_id") Integer userId) {
//        List<UserScoredResponse> data = userReportService.getScoredMovies(userId);
//        ListResponse<List<UserScoredResponse>> response = new ListResponse<>();
//        response.setList(data);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

























}
