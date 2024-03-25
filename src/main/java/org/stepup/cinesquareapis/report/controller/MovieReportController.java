package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.report.model.MovieCommentReplyRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;
import org.stepup.cinesquareapis.report.service.MovieReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "movie-report", description = "영화 리뷰 API")
@RequestMapping("api/movies-report")
public class MovieReportController {

    private final MovieReportService movieReportService;

    /**
     * 사용자 리뷰, 점수 조회
     *
     * table : tb_movie_comment_summary
     * page : 영화 단건 선택한 페이지
     */
//    @Operation(summary = "사용자 리뷰, 점수 조회")
//    @GetMapping("{movieId}")
//    public ResponseEntity<List<ReadMovieReportResponse>> readMovieUsersReport(@PathVariable("movieId") Integer movieId) {
//        List<ReadMovieReportResponse> result = movieReportService.readMovieUsersReport(movieId);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    /**
     * 영화 코멘트 작성/수정
     * <p>
     * table : tb_movie_comment
     */
    @Operation(summary = "영화 코멘트 작성")
    @PostMapping("comment")
    public ResponseEntity<Boolean> saveComment(@RequestBody MovieCommentRequest request) {
        movieReportService.saveComment(request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 답굴 작성/수정
     * <p>
     * table : tb_movie_comment_reply
     */
    @Operation(summary = "영화 코멘트 답글 부여/수정")
    @PostMapping("reply")
    public ResponseEntity<Boolean> saveCommentReply(@RequestBody MovieCommentReplyRequest request) {
        movieReportService.saveCommentReply(request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 + 점수 (api 호출용)")
    @GetMapping("summary/movies/{movieId}/comments/{commentId}/users/{userId}")
    public ResponseEntity<List<MovieCommentSummaryResponse>> searchCommentSummary(@PathVariable("movieId") Integer movieId, @PathVariable("commentId") Integer commentId, @PathVariable("userId") Integer userId) {
        List<MovieCommentSummaryResponse> responses = movieReportService.searchCommentSummary(userId, movieId, commentId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

























}
