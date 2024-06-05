package org.stepup.cinesquareapis.report.controller;


import io.swagger.v3.oas.annotations.Hidden;
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
import org.stepup.cinesquareapis.common.dto.*;
import org.stepup.cinesquareapis.report.dto.*;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.service.UserReportService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "4 user-reports", description = "유저별 영화 리뷰 API (코멘트 API 검토중)")
@RequestMapping("api/user-reports")
public class UserReportController {

    private final UserReportService userReportService;

    /**
     * 내가 평가한 영화 별점 조회
     *
     * table: tb_user_movie_score
     */
    @Operation(summary = "내가 평가한 영화 별점 조회",
            description = "- 별점을 부과하지 않은 경우: null\n" +
                    "- 그 외: 0.5~5")
    @UserAuthorize
    @GetMapping("me/movies/{movie_id}/score")
    public ResponseEntity<DataResponse<UserMovieScoreResponse>> getUserMovieScore(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId) {
        int userId = Integer.parseInt(principal.getUsername());

        UserMovieScoreResponse data = userReportService.getUserMovieScore(userId, movieId);
        DataResponse<UserMovieScoreResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * [사용 불가] 유저별 평가한 영화 별점 조회
     *
     * table: tb_user_movie_score
     */
    @Hidden
    @Operation(summary = "[사용 불가] 유저별 평가한 영화 별점 조회",
            description = "- 별점을 부과하지 않은 경우: null\n" +
                    "- 그 외: 0.5~5")
    @GetMapping("{user_id}/movies/{movie_id}/score")
    public ResponseEntity<DataResponse<UserMovieScoreResponse>> getUserMovieScore(@PathVariable("movie_id") int user_id, @PathVariable("movie_id") int movieId) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    /**
     * 영화 별점 부과
     *
     * table: tb_user_movie_score, tb_movie_summary, tb_movie_simple
     */
    @Operation(summary = "영화 별점 부과",
            description = "- 요청 필수값 : score\n" +
                    "- 최초 별점 부과: 0.5단위로 0.5~5\n" +
                    "- 이미 값이 있는 경우: 수정 API 요청\n" +
                    "- 부과한 별점을 취소하는 경우: 삭제 API 요청")
    @ApiResponses({
        @ApiResponse(responseCode = "40004", description = "이미 존재하는 경우 에러코드 (수정 API로 요청)", content = @Content()),
        @ApiResponse(responseCode = "40006", description = "score 범위가 허용하지 않는 값인 경우", content = @Content()),
        @ApiResponse(responseCode = "40007", description = "영화 테이블 score 업데이트 실패 (기존 데이터가 없는 경우)", content = @Content())
    })
    @UserAuthorize
    @PostMapping("-/movies/{movie_id}/score")
    public ResponseEntity<DataResponse<UserMovieScoreResponse>> saveUserMovieScore(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId, @RequestBody UserScoreRequest request) {
        int userId = Integer.parseInt(principal.getUsername());

        UserMovieScoreResponse data = userReportService.saveUserMovieScore(userId, movieId, request);

        DataResponse<UserMovieScoreResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 별점 수정
     *
     * table: tb_user_movie_score, tb_movie_summary, tb_movie_simple
     */
    @Operation(summary = "영화 별점 수정",
            description = "- 요청 필수값: score")
    @ApiResponses({
            @ApiResponse(responseCode = "40005", description = "존재하지 않는 경우 (저장 API로 요청)", content = @Content()),
            @ApiResponse(responseCode = "40007", description = "영화 테이블 score 업데이트 실패 (기존 데이터가 없는 경우)", content = @Content())
    })
    @UserAuthorize
    @PatchMapping("-/movies/{movie_id}/score")
    public ResponseEntity<DataResponse<UserMovieScoreResponse>> updateScore(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId, @RequestBody UserScoreRequest request) {
        int userId = Integer.parseInt(principal.getUsername());

        UserMovieScoreResponse data = userReportService.updateScore(userId, movieId, request);
        DataResponse<UserMovieScoreResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 별점 삭제
     *
     * table: tb_user_movie_score, tb_movie_summary, tb_movie_simple
     */
    @Operation(summary = "영화 별점 삭제")
    @UserAuthorize
    @DeleteMapping("-/movies/{movie_id}/score")
    public ResponseEntity<HttpStatus> deleteScore(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId) {
        int userId = Integer.parseInt(principal.getUsername());

        userReportService.deleteScore(userId, movieId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 내가 평가한 영화 개수 조회
     *
     * table(view): tv_user_movie_score
     */
    @Operation(summary = "내가 평가한 영화 개수 조회",
            description = "https://pedia.watcha.com/ko-KR/review 평가하기 영화 별점 개수 조회 + 마이페이지 상단")
    @UserAuthorize
    @GetMapping("me/movies/scored-counts")
    public ResponseEntity<DataResponse<Integer>> getScoredCount(@AuthenticationPrincipal User principal) {
        int userId = Integer.parseInt(principal.getUsername());

        int data = userReportService.getScoredCount(userId);
        DataResponse<Integer> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * [사용 불가] 유저별 평가한 영화 개수 조회
     *
     * table(view): tv_user_movie_score
     */
    @Operation(summary = "[사용 불가] 유저별 평가한 영화 개수 조회",
            description = "https://pedia.watcha.com/ko-KR/review 평가하기 영화 별점 개수 조회 + 마이페이지 상단")
    @GetMapping("{user_id}/movies/scored-counts")
    public ResponseEntity<DataResponse<Integer>> getScoredCount(@PathVariable("movie_id") int user_id) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    /**
     * 내가 평가한 영화 목록 조회
     *
     * table(view): tv_user_movie_score
     */
    @Operation(summary = "내가 평가한 영화 목록 조회 (페이징)")
    @UserAuthorize
    @GetMapping("me/movies/scored")
    public ResponseEntity<PageResponse<List<UserScoredMovies>>> getScoredMovies(@AuthenticationPrincipal User principal,
                                                                                @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        int userId = Integer.parseInt(principal.getUsername());

        //페이지 셋팅
        Pageable pageable = PageRequest.of(page-1, size);

        Page<UserScoredMovies> pagedData = userReportService.getScoredMovies(userId, pageable);
        PageResponse<List<UserScoredMovies>> response = new PageResponse<>(page, size);
        response.setList(pagedData.getContent()); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1: pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * [사용 불가] 유저별 평가한 영화 목록 조회
     *
     * table(view): tv_user_movie_score
     */
    @Operation(summary = "[사용 불가] 유저별 평가한 영화 목록 조회 (페이징)")
    @GetMapping("{user_id}/movies/scored")
    public ResponseEntity<PageResponse<List<UserScoredMovies>>> getScoredMovies(@PathVariable("movie_id") int user_id) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    /**
     * 내가 평가한 영화 별점 분포 조회
     *
     */
    @Operation(summary = "내가 평가한 영화 별점 분포 조회",
            description = "- 마이페이지\n" +
                    "?? 우선 있는 경우 해당 별점, 개수 이렇게 표기 됨. 추후에 score는 기본값으로 셋팅 예정")
    @UserAuthorize
    @GetMapping("me/movies/scored-distribution")
    public ResponseEntity<ListResponse<List<UserMovieRating>>> getUserMovieRating(@AuthenticationPrincipal User principal) {
        int userId = Integer.parseInt(principal.getUsername());

        List<UserMovieRating> list = userReportService.getUserMovieRating(userId);
        ListResponse<List<UserMovieRating>> response = new ListResponse<>();
        response.setList(list);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * [사용 불가] 유저별 평가한 영화 별점 분포 조회
     *
     */
    @Operation(summary = "[사용 불가] 유저별 평가한 영화 별점 분포 조회")
    @GetMapping("{user_id}/movies/scored-distribution")
    public ResponseEntity<ListResponse<List<UserMovieRating>>> getUserMovieRating(@PathVariable("user_id") int user_id) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    /**
     * 내 영화 상태(보고싶어요) 조회
     *
     */
    @Operation(summary = "내 영화 상태(보고싶어요) 조회",
            description = "- 상태가 변경되어있는 경우(보고싶어요 상태) return : true\n" +
                    "- 상태가 변경되어있지 않은 경우(아무 상태 아닌경우) return : false")
    @UserAuthorize
    @GetMapping("me/movies/{movie_id}/status")
    public ResponseEntity<ResultResponse<Boolean>> getMovieUserStatus(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId) {
        int userId = Integer.parseInt(principal.getUsername());

        Boolean data = userReportService.getMovieUserStatus(userId, movieId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * [사용 불가] 영화 상태(보고싶어요) 조회
     *
     */
    @Operation(summary = "[사용 불가] 유저별 영화 상태(보고싶어요) 조회",
            description = "- 상태가 변경되어있는 경우(보고싶어요 상태) return : true\n" +
                    "- 상태가 변경되어있지 않은 경우(아무 상태 아닌경우) return : false")
    @UserAuthorize
    @GetMapping("{user_id}/movies/{movie_id}/status")
    public ResponseEntity<ResultResponse<Boolean>> getMovieUserStatus(@PathVariable("user_id") int user_id, @PathVariable("movie_id") int movie_id) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    /**
     * 내 영화 상태(보고싶어요) 리스트 조회
     *
     * table: tb_user_movie_status
     */
    @Operation(summary = "영화 상태(보고싶어요) 리스트 조회")
    @UserAuthorize
    @GetMapping("me/movies/-/status")
    public ResponseEntity<PageResponse2<Integer>> getStatusList(@AuthenticationPrincipal User principal
            , @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        Integer userId = Integer.parseInt(principal.getUsername());

        // 페이지 셋팅
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Integer> pagedData = userReportService.getMovieUserStatusList(userId, pageable);

        // PageResponse 객체 생성
        PageResponse2<Integer> response = new PageResponse2(pagedData, page, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 상태(보고싶어요) 저장
     *
     * table: tb_user_movie_status
     */
    @Operation(summary = "영화 상태(보고싶어요) 저장",
            description = "- 요청 필수값 : movie_id\n" +
                    "- 기본 Flow : 해당 테이블에 데이터가 있으면 '보고싶어요' 상태\n" +
                    "- 존재하지 않으면 아무 상태도 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "500", description = "저장에 실패하였을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40008", description = "이미 등록된 상태의 경우 에러코드 (삭제 API 요청)", content = @Content())
    })
    @UserAuthorize
    @PostMapping("-/movies/{movie_id}/status")
    public ResponseEntity<HttpStatus> saveScore(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId) {
        int userId = Integer.parseInt(principal.getUsername());

        Boolean data = userReportService.saveStatus(userId, movieId);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 영화 상태(보고싶어요) 삭제
     *
     * table: tb_user_movie_status
     */
    @Operation(summary = "영화 상태(보고싶어요) 삭제",
            description = "- 요청 필수값 : movie_id\n" +
                    "- 기본 Flow : 해당 테이블에 데이터가 있으면 '보고싶어요' 상태\n"+
                    "- 존재하지 않으면 아무 상태도 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @UserAuthorize
    @DeleteMapping("-/movies/{movie_id}/status")
    public ResponseEntity<HttpStatus> deleteStatus(@AuthenticationPrincipal User principal, @PathVariable("movie_id") int movieId) {
        int userId = Integer.parseInt(principal.getUsername());

        Boolean data = userReportService.deleteStatus(userId, movieId);
        if (data) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    /**
     * 영화 코멘트 저장
     *
     * table : tb_movie_comment
     *
     * @return Comment
     */
    @Operation(summary = "영화 코멘트 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "40000", description = "코멘트가 이미 존재하는 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @PostMapping("-/movies/{movie_id}/comments")
    public ResponseEntity<DataResponse<MovieCommentResponse>> saveComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @RequestBody MovieCommentSaveRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        MovieCommentResponse response = userReportService.saveComment(request, movieId, userId);

        return ResponseEntity.ok(new DataResponse(response));
    }
    
    /**
     * 영화 코멘트 수정
     *
     * table : tb_movie_comment
     *
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 수정",
            description = "- 요청 필수값 : comment_id, user_id")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "코멘트를 수정할 수 없는 유저가 요청하는 경우의 에러코드"),
            @ApiResponse(responseCode = "404", description = "파라미터 값을 잘못 요청하는 경우의 에러코드"),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @PatchMapping("-/movies/{movie_id}/comments/{comment_id}")
    public ResponseEntity<DataResponse<MovieCommentResponse>> updateComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId, @RequestBody MovieCommentUpdateRequest request) {
        Integer userId = Integer.parseInt(principal.getUsername());

        MovieCommentResponse response = userReportService.updateComment(request, movieId, commentId, userId);

        return ResponseEntity.ok(new DataResponse(response));
    }

    /**
     * 영화 코멘트 삭제
     *
     * table : tb_movie_comment
     *
     * @return true, false
     */
    @Operation(summary = "영화 코멘트 삭제",
            description = "- 요청 필수값 : comment_id, user_id\n" +
                    "- 기본 Flow : 코멘트가 삭제되면, 하위 답글과 코멘트 좋아요 도 함께 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "정상적으로 삭제 되었을 경우 HTTP 상태코드"),
            @ApiResponse(responseCode = "403", description = "코멘트를 수정할 수 없는 유저가 요청하는 경우의 에러코드"),
            @ApiResponse(responseCode = "404", description = "파라미터 값을 잘못 요청하는 경우의 에러코드"),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content())
    })
    @UserAuthorize
    @DeleteMapping("-/movies/{movie_id}/comments/{comment_id}")
    public ResponseEntity deleteComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        userReportService.deleteComment(userId, movieId, commentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 유저의 영화별 코멘트 조회
     *
     */
    @Operation(summary = "유저의 영화별 코멘트 조회")
    @UserAuthorize
    @GetMapping("-/movies/{movie_id}/comment")
    public ResponseEntity<DataResponse<MovieCommentResponse>> getMovieComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        MovieCommentResponse response = userReportService.getMovieComment(userId, movieId);

        if (response.getCommentId() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new DataResponse(response));
    }

    /**
     * 영화별 유저가 좋아요한 코멘트 목록 조회
     *
     */
    @Operation(summary = "영화별 유저가 좋아요한 코멘트 목록 조회 (페이징)",
            description = "user_id, movie_id 기준으로 comment_id 목록 조회<br>" +
                    "return 값 : comment_id 리스트, 해당 목록을 가지고 코멘트 목록에서 좋아요 했는지 비교해서 판단해야됨!!")
    @UserAuthorize
    @GetMapping("-/movies/{movie_id}/like-comments")
    public ResponseEntity<ListResponse<LikedMovieCommentResponse>> getUserLikeCommentList(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        LikedMovieCommentResponse[] response = userReportService.getUserMovieLikeCommentList(userId, movieId);

        return ResponseEntity.ok(new ListResponse(response));
    }

    /**
     * 좋아요한 코멘트 목록
     *
     */
    @Operation(summary = "좋아요한 코멘트 목록 (페이징)")
    @UserAuthorize
    @GetMapping("like-comments")
    public ResponseEntity<PageResponse<List<LikeCommentsResponse>>> getLikeComments(@AuthenticationPrincipal User principal,
                                                                 @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        Integer userId = Integer.parseInt(principal.getUsername());

        //페이지 셋팅
        Pageable pageable = PageRequest.of(page-1, size);

        Page<CommentSummary> pagedData = userReportService.getUserLikeComments(userId, pageable);
        PageResponse<List<LikeCommentsResponse>> response = new PageResponse<>(page, size);
        response.setList(pagedData.getContent().stream().map(LikeCommentsResponse::new).collect(Collectors.toList())); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1: pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 코멘트 좋아요 등록
     */
    @Operation(summary = "유저별 코멘트 좋아요 등록",
            description = "코멘트 좋아요 등록 시 해당 user의 comment 테이블에 like count 증가<br>")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "500", description = "저장에 실패하였을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "40001", description = "코멘트가 존재하지 않는 경우 에러코드", content = @Content()),
            @ApiResponse(responseCode = "40009", description = "이미 등록된 상태의 경우 에러코드 (삭제 API 요청)", content = @Content())
    })
    @UserAuthorize
    @PostMapping("movies/{movie_id}/like-comments/{comment_id}")
    public ResponseEntity<ResultResponse<Boolean>> saveLikeComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        Boolean data = userReportService.saveLikeComment(userId, movieId, commentId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 유저별 코멘트 좋아요 삭제
     */
    @Operation(summary = "유저별 코멘트 좋아요 삭제",
            description = "코멘트 좋아요 삭제 시 해당 user의 comment 테이블에 like count 감소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 저장 되었을 경우 HTTP 상태코드", content = @Content()),
            @ApiResponse(responseCode = "404", description = "데이터가 없는 경우 HTTP 상태코드", content = @Content())
    })
    @UserAuthorize
    @DeleteMapping("movies/{movie_id}/like-comments/{comment_id}")
    public ResponseEntity<ResultResponse<Boolean>> deleteLikeComment(@AuthenticationPrincipal User principal, @PathVariable("movie_id") Integer movieId, @PathVariable("comment_id") Integer commentId) {
        Integer userId = Integer.parseInt(principal.getUsername());

        Boolean data = userReportService.deleteLikeComment(userId, movieId, commentId);
        ResultResponse<Boolean> response = new ResultResponse<>();
        response.setResult(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 좋아요 한 코멘트 개수 조회
     *
     */
    @Operation(summary = "좋아요 한 코멘트 개수 조회",
            description = "마이피이지 하단")
    @UserAuthorize
    @GetMapping("like-comment-counts")
    public ResponseEntity<DataResponse<Integer>> getLikeCommentCounts(@AuthenticationPrincipal User principal) {
        Integer userId = Integer.parseInt(principal.getUsername());

        int data = userReportService.getLikeCommentCounts(userId);
        DataResponse<Integer> response = new DataResponse<>();
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
