package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.model.DataResponse;
import org.stepup.cinesquareapis.common.model.ListResponse;
import org.stepup.cinesquareapis.common.model.ResultResponse;
import org.stepup.cinesquareapis.report.model.MovieCommentReplyRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;
import org.stepup.cinesquareapis.report.model.MovieReplyResponse;
import org.stepup.cinesquareapis.report.service.MovieReportService;
import org.stepup.cinesquareapis.user.model.UserResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "movie-report", description = "영화 리뷰 API")
@RequestMapping("api/movie-reports")
public class MovieReportController {

    private final MovieReportService movieReportService;

    /**
     * 영화 코멘트 작성/수정
     * <p>
     * table : tb_movie_comment
     *
     * - comment_id가 이상한 값으로 들어오는 경우, 자동으로 생성을 하게됨 => false
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 작성/수정")
    @PostMapping("comment")
    public ResponseEntity<ResultResponse<Boolean>> saveComment(@RequestBody MovieCommentRequest request) {
        Boolean data = movieReportService.saveComment(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<ResultResponse<Boolean>>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 답굴 작성/수정
     * <p>
     * table : tb_movie_comment_reply
     *
     * - reply_id가 이상한 값으로 들어오는 경우, 자동으로 생성을 하게됨 => false
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 답글 작성/수정")
    @PostMapping("reply")
    public ResponseEntity<ResultResponse<Boolean>> saveCommentReply(@RequestBody MovieCommentReplyRequest request) {
        Boolean data = movieReportService.saveCommentReply(request);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * @param movie_id
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 + 점수 (api 호출용) \r\n " +
            "https://pedia.watcha.com/ko-KR/contents/m5x1xva/comments 페이지" )
    @GetMapping("summary/movies/{movie_id}")
    public ResponseEntity<ListResponse<List<MovieCommentSummaryResponse>>> searchCommentSummary(@PathVariable("movie_id") Integer movieId) {
        List<MovieCommentSummaryResponse> data = movieReportService.searchCommentSummary(movieId);
        ListResponse<List<MovieCommentSummaryResponse>> response = new ListResponse<>();
        response.setList(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 상세 및 답글 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 상세 및 답글 조회\r\n " +
            "https://pedia.watcha.com/ko-KR/contents/m5x1xva/comments 페이지" )
    @GetMapping("comments/{comment_id}")
    public ResponseEntity<ListResponse<List<MovieReplyResponse>>> searchReplyList(@PathVariable("comment_id") Integer commentId) {
        List<MovieReplyResponse> data = movieReportService.searchReplyList(commentId);
        ListResponse<List<MovieReplyResponse>> response = new ListResponse<>();
        response.setList(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

























}
