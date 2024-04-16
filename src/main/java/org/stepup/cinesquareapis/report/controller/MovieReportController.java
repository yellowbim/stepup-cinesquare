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
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.model.*;
import org.stepup.cinesquareapis.report.service.MovieReportService;

import javax.xml.transform.Result;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "movie-reports", description = "영화 리뷰 API")
@RequestMapping("api/movie-reports")
public class MovieReportController {

    private final MovieReportService movieReportService;

    /**
     * 영화 코멘트 등록
     * <p>
     * table : tb_movie_comment
     *
     * @return Comment
     */
    @Operation(summary = "영화 코멘트 등록",
                description = "요청 필수값 : user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 등록 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40000", description = "코멘트가 이미 존재하는 경우 에러코드", content = @Content())
    })
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
            description = "요청 필수값 : comment_id, user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 수정 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content())
    })
    @PatchMapping("{movie_id}/comments/{comment_id}")
    public ResponseEntity<DataResponse<Comment>> updateComment(@RequestBody MovieCommentUpdateRequest request, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Comment data = movieReportService.updateComment(request, movieId, commentId);
        DataResponse<Comment> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 삭제
     * <p>
     * table : tb_movie_comment
     *
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 삭제",
            description = "요청 필수값 : comment_id, user_id는 추후에 필요없는 데이터인데 일단 필요해서 해당 url에 포함시켜놓음<br>" +
                    "기본 Flow : 코멘트가 삭제되면 이에따른 코멘트 답글, 코멘트 좋아요도 불필요한 데이터라 삭제됨!!!!")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 삭제 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @DeleteMapping("{movie_id}/comments/{comment_id}/{user_id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable("movie_id") Integer movieId, @PathVariable("user_id") Integer userId, @PathVariable("comment_id") Integer commentId) {
        int data = movieReportService.deleteComment(commentId);
        if (data > 0) { //정상 삭제
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 영화 코멘트 답글 작성
     * <p>
     * table : tb_movie_comment_reply
     *
     * @return CommentReply
     */
    @Operation(summary = "영화 코멘트 답글 등록",
                description = "요청 필수값 : user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 등록 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content()),
            @ApiResponse(responseCode = "40002", description = "코멘트 답변이 이미 등록된 경우 에러코드", content = @Content())
    })
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
                description = "요청 필수값 : reply_id, user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 수정 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40003", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
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
    @Operation(summary = "영화 코멘트 답글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 삭제 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @DeleteMapping("{movie_id}/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<HttpStatus> deleteCommentReply(@PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        int data = movieReportService.deleteCommentReply(commentId, movieId, replyId);
        if (data > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else  {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * @param movie_id
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 + 점수 (api 호출용)",
                description = "https://pedia.watcha.com/ko-KR/contents/mWz3rPP 코멘트 목록<br>" +
                                "https://pedia.watcha.com/ko-KR/comments/NXnE5gwnkyMzG 코멘트 상세 항목")
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
    @Operation(summary = "영화 코멘트 상세 및 답글 조회",
                description = "https://pedia.watcha.com/ko-KR/comments/NXnE5gwnkyMzG 코멘트 답글 조회")
    @GetMapping("comments/{comment_id}")
    public ResponseEntity<ListResponse<List<MovieReplyResponse>>> searchReplyList(@PathVariable("comment_id") Integer commentId) {
        List<MovieReplyResponse> data = movieReportService.searchReplyList(commentId);
        ListResponse<List<MovieReplyResponse>> response = new ListResponse<>();
        response.setList(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

























}
