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
import org.stepup.cinesquareapis.common.dto.PageResponse2;
import org.stepup.cinesquareapis.common.dto.ResultResponse;
import org.stepup.cinesquareapis.report.dto.*;
import org.stepup.cinesquareapis.report.entity.MovieCommentSummary;
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
     * 코멘트 조회
     */
    @Operation(summary = "코멘트 조회")
    @ApiResponses({
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
     * 영화 코멘트별 답글 목록 조회 (페이징)
     *
     * table : tb_comment_reply
     */
    @Operation(summary = "영화 코멘트별 답글 목록 조회 (페이징)",
            description = "https://pedia.watcha.com/ko-KR/comments/NXnE5gwnkyMzG 코멘트 답글 조회")
    @GetMapping("-/comments/{comment_id}/replies")
    public ResponseEntity<PageResponse2<CommentReplyResponse>> searchReplyList(@PathVariable("comment_id") Integer commentId
            , @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {

        // 페이지 셋팅
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CommentReplyResponse> response = movieReportService.getCommentReplyList(commentId, pageable);

        return ResponseEntity.ok(new PageResponse2(response, page, size));
    }

    /**
     * 영화 코멘트 답글 등록
     *
     * table : tb_comment_reply
     *
     * @return CommentReply
     */
    @Operation(summary = "영화 코멘트 답글 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @PostMapping("-/comments/{comment_id}/replies")
    public ResponseEntity<DataResponse<CommentReplyResponse>> saveCommentReply(@AuthenticationPrincipal User principal
            , @PathVariable("comment_id") Integer commentId, @RequestBody SaveCommentReplyRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        CommentReplyResponse response = movieReportService.saveCommentReply(request, commentId, userId);

        return ResponseEntity.ok(new DataResponse(response));
    }

    /**
     * 영화 코멘트 답글 수정
     *
     * table : tb_comment_reply
     */
    @Operation(summary = "영화 코멘트 답글 수정",
            description = "요청 필수값 : reply_id, user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "코멘트를 수정할 수 없는 유저가 요청하는 경우의 에러코드"),
            @ApiResponse(responseCode = "40003", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @UserAuthorize
    @PatchMapping("-/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<DataResponse<CommentReplyResponse>> updateCommentReply(@AuthenticationPrincipal User principal, @RequestBody UpdateCommentReplyRequest request
            , @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        CommentReplyResponse response = movieReportService.updateCommentReply(request, commentId, replyId, userId);

        return ResponseEntity.ok(new DataResponse(response));
    }

    /**
     * 영화 코멘트 답글 삭제
     *
     * table : tb_comment_reply
     */
    @Operation(summary = "영화 코멘트 답글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "정상적으로 삭제 되었을 경우의 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40001", description = "코멘트 데이터가 없는 경우의 에러코드", content = @Content()),
            @ApiResponse(responseCode = "40003", description = "답글 데이터가 없는 경우의 에러코드", content = @Content()),
    })
    @UserAuthorize
    @DeleteMapping("-/comments/{comment_id}/replies/{reply_id}")
    public ResponseEntity<HttpStatus> deleteCommentReply(@AuthenticationPrincipal User principal
            , @PathVariable("comment_id") Integer commentId, @PathVariable("reply_id") Integer replyId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        movieReportService.deleteCommentReply(commentId, replyId, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * @param movie_id table : tb_movie_comment_summary
     */
    @Operation(summary = "영화 코멘트 리스트 + 점수 (페이징)",
            description = "기본적으로 영화만 기준으로 코멘트 목록을 조회하는 기능<br>" +
                    "https://pedia.watcha.com/ko-KR/contents/mWz3rPP")
    @GetMapping("summary/movies/{movie_id}")
    public ResponseEntity<PageResponse<List<MovieCommentSummaryResponse>>> searchCommentSummary(@PathVariable("movie_id") Integer movieId,
                                                                                                @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {

        // 페이지 셋팅 (Pageable은 0부터 시작해서 인입값 -1로 셋팅 필요)
        Pageable pageable = PageRequest.of(page - 1, size);
        // PageResponse 생성(초기화)
        PageResponse<List<MovieCommentSummaryResponse>> response = new PageResponse<>(page, size);

        Page<MovieCommentSummary> pagedData = movieReportService.searchCommentSummary(movieId, pageable);
        response.setList(pagedData.getContent().stream().map(MovieCommentSummaryResponse::new).collect(Collectors.toList())); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1 : pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 사용자가 평가한 코멘트 개수 조회
     *
     * @param comment_id table : tb_movie_comment_summary
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