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
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.model.*;
import org.stepup.cinesquareapis.report.service.MovieReportService;

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
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 작성")
    @PostMapping("{movie_id}/comments")
    public ResponseEntity<DataResponse<Comment>> saveComment(@RequestBody MovieCommentSaveRequest request, @PathVariable("movie_id") Integer movieId) {
        Comment data = movieReportService.saveComment(request, movieId);
        DataResponse<Comment> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 수정
     * <p>
     * table : tb_movie_comment
     *
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 수정",
            description = "정상 처리 시 데이터 전부 return, \n 없는 경우 null 전달")
    @PatchMapping("{movie_id}/comments/{comment_id}")
    public ResponseEntity<DataResponse<Comment>> updateComment(@RequestBody MovieCommentUpdateRequest request, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Comment data = movieReportService.updateComment(request, movieId, commentId);
        DataResponse<Comment> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 답글 작성
     * <p>
     * table : tb_movie_comment_reply
     *
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 답글 작성")
    @PostMapping("{movie_id}/comments/{comment_id}/replies")
    public ResponseEntity<DataResponse<CommentReply>> saveCommentReply(@RequestBody MovieCommentReplySaveRequest request, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        CommentReply data = movieReportService.saveCommentReply(request, commentId, movieId);
        DataResponse<CommentReply> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 답글 수정
     * <p>
     * table : tb_movie_comment_reply
     *
     * - reply_id가 이상한 값으로 들어오는 경우, 자동으로 생성을 하게됨 => false
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 답글 수정",
                description = "정상 처리 시 데이터 전부 return, \n 없는 경우 null 전달")
    @PatchMapping("{movie_id}/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<DataResponse<CommentReply>> updateCommentReply(@RequestBody MovieCommentReplyUpdateRequest request, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        CommentReply data = movieReportService.updateCommentReply(request, commentId, movieId, replyId);
        DataResponse<CommentReply> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 답글 삭제
     * <p>
     * table : tb_movie_comment_reply
     *
     * - reply_id가 이상한 값으로 들어오는 경우, 자동으로 생성을 하게됨 => false
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 답글 수정",
                description = "정상 처리 시 return 1, 데이터가 없는 경우 0")
    @DeleteMapping("{movie_id}/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<ResultResponse<Integer>> deleteCommentReply(@PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        int data = movieReportService.deleteCommentReply(commentId, movieId, replyId);
        ResultResponse<Integer> response = new ResultResponse<>();
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
