package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.annotation.UserAuthorize;
import org.stepup.cinesquareapis.common.dto.DataResponse;
import org.stepup.cinesquareapis.common.dto.PageResponse;
import org.stepup.cinesquareapis.common.dto.ResultResponse;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.dto.*;
import org.stepup.cinesquareapis.report.service.MovieReportService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "5 movie-reports", description = "영화 리뷰 API")
@RequestMapping("api/movie-reports")
public class MovieReportController {

    private final MovieReportService movieReportService;

    /**
     * 유저가 영화 1건에 남긴 코멘트 조회
     *
     */
    @Operation(summary = "사용자 본인이 남긴 코멘트가 아닌 경우 상세 코멘트를 조회하는 API ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "조회된 코멘트가 없는경우", content = @Content()),
            @ApiResponse(responseCode = "500", description = "조회에 실패하는경우 HTTP 상태코드", content = @Content())
    })
    @GetMapping("{movie_id}/comments/{comment_id}")
    public ResponseEntity<ResultResponse<MovieCommentResponse>> getMovieComment(@PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {

        MovieCommentResponse data = movieReportService.getMovieComment(movieId, commentId);
        ResultResponse<MovieCommentResponse> response = new ResultResponse<>();

        if (data.getCommentId() == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 등록
     * <p>
     * table : tb_movie_comment
     *
     * @return Comment
     */
    @Operation(summary = "영화 코멘트 등록",
                description = "사용자가 영화에 대하여 코멘트를 등록하는 기능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 등록 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40000", description = "코멘트가 이미 존재하는 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @PostMapping("{movie_id}/comments")
    public ResponseEntity<DataResponse<Comment>> saveComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @RequestBody MovieCommentSaveRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        Comment data = movieReportService.saveComment(request, movieId, userId);
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
    @UserAuthorize
    @PatchMapping("{movie_id}/comments/{comment_id}")
    public ResponseEntity<DataResponse<Comment>> updateComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @RequestBody MovieCommentUpdateRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        Comment data = movieReportService.updateComment(request, movieId, commentId, userId);
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
    @UserAuthorize
    @DeleteMapping("{movie_id}/comments/{comment_id}")
    public ResponseEntity<HttpStatus> deleteComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Integer userId = Integer.parseInt(principal.getUsername());

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
            description = "코멘트 답글 등록 시 해당 user의 comment 테이블에 reply count 증가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 등록 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content()),
            @ApiResponse(responseCode = "40002", description = "코멘트 답변이 이미 등록된 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @PostMapping("{movie_id}/comments/{comment_id}/replies")
    public ResponseEntity<DataResponse<CommentReply>> saveCommentReply(@RequestBody MovieCommentReplySaveRequest request, @AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        CommentReply data = movieReportService.saveCommentReply(request, commentId, movieId, userId);
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
    @UserAuthorize
    @PatchMapping("{movie_id}/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<DataResponse<CommentReply>> updateCommentReply(@RequestBody MovieCommentReplyUpdateRequest request, @AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        CommentReply data = movieReportService.updateCommentReply(request, commentId, movieId, replyId, userId);
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
    @Operation(summary = "영화 코멘트 답글 삭제",
            description = "코멘트 답글 삭제 시 해당 user의 comment 테이블에 reply count 감소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 삭제 되었을 경우 HTTP 상태코드", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @UserAuthorize
    @DeleteMapping("{movie_id}/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<HttpStatus> deleteCommentReply(@PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId, @AuthenticationPrincipal User principal) {
        Integer userId = Integer.parseInt(principal.getUsername());

        int data = movieReportService.deleteCommentReply(commentId, movieId, replyId, userId);
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
    @Operation(summary = "영화 코멘트 리스트 + 점수 (페이징)",
                description = "기본적으로 영화만 기준으로 코멘트 목록을 조회하는 기능<br>" +
                                "https://pedia.watcha.com/ko-KR/contents/mWz3rPP")
    @GetMapping("summary/movies/{movie_id}")
    public ResponseEntity<PageResponse<List<MovieCommentSummaryResponse>>> searchCommentSummary(@PathVariable("movie_id") Integer movieId,
                    @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {

        // 페이지 셋팅 (Pageable은 0부터 시작해서 인입값 -1로 셋팅 필요)
        Pageable pageable = PageRequest.of(page-1, size);
        // PageResponse 생성(초기화)
        PageResponse<List<MovieCommentSummaryResponse>> response = new PageResponse<>(page, size);

        Page<CommentSummary> pagedData = movieReportService.searchCommentSummary(movieId, pageable);
        response.setList(pagedData.getContent().stream().map(MovieCommentSummaryResponse::new).collect(Collectors.toList())); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1: pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 영화 코멘트 상세 및 답글 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 답글 조회 (페이징)",
                description = "https://pedia.watcha.com/ko-KR/comments/NXnE5gwnkyMzG 코멘트 답글 조회")
    @GetMapping("comments/{comment_id}")
    public ResponseEntity<PageResponse<List<MovieReplyResponse>>> searchReplyList(@PathVariable("comment_id") Integer commentId,
                      @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {

        // 페이지 셋팅 (Pageable은 0부터 시작해서 인입값 -1로 셋팅 필요)
        Pageable pageable = PageRequest.of(page-1, size);
        // PageResponse 생성(초기화)
        PageResponse<List<MovieReplyResponse>> response = new PageResponse<>(page, size);

        Page<CommentReply> pagedData = movieReportService.searchReplyList(commentId, pageable);
        response.setList(pagedData.getContent().stream().map(MovieReplyResponse::new).collect(Collectors.toList())); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1: pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 사용자가 평가한 코멘트 개수 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    @Operation(summary = "사용자가 평가한 코멘트 개수 조회 (mypage)")
    @UserAuthorize
    @GetMapping("comments/counts")
    public ResponseEntity<DataResponse<Integer>> getUserCommentCounts(@AuthenticationPrincipal User principal) {
        Integer userId = Integer.parseInt(principal.getUsername());

        int data = movieReportService.getUserCommentCount(userId);
        DataResponse<Integer> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
