package org.stepup.cinesquareapis.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CommonErrorCode;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.report.dto.CommentReplyResponse;
import org.stepup.cinesquareapis.report.dto.MovieCommentResponse;
import org.stepup.cinesquareapis.report.dto.SaveCommentReplyRequest;
import org.stepup.cinesquareapis.report.dto.UpdateCommentReplyRequest;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.entity.MovieComment;
import org.stepup.cinesquareapis.report.entity.MovieCommentSummary;
import org.stepup.cinesquareapis.report.repository.MovieCommentReplyRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentSummaryRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieReportService {

    private final MovieCommentRepository movieCommentRepository;
    private final MovieCommentReplyRepository movieCommentReplyRepository;
    private final MovieCommentSummaryRepository movieCommentSummaryRepository;

    /**
     * 사용자 본인이 남긴 코멘트가 아닌 경우 상세 코멘트를 조회하는 API
     */
    public MovieCommentResponse getMovieComment(Integer movieId, Integer commentId) {
        MovieComment movieComment = movieCommentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT));

        // 유효성 체크: 영화 확인
        if (!movieComment.getMovieId().equals(movieId)) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST);
        }

        return new MovieCommentResponse(movieComment);
    }

    /**
     * 영화 코멘트별 답글 목록 조회 (페이징)
     *
     * @param commentId, pageable
     * table : tb_movie_comment_summary
     */
    public Page<CommentReplyResponse> getCommentReplyList(Integer commentId, Pageable pageable) {
        Page<CommentReply> pagedCommentReplies = movieCommentReplyRepository.findAllByCommentId(commentId, pageable);
        
        return pagedCommentReplies.map(CommentReplyResponse::new);
    }

    /**
     * 영화 코멘트 답글 작성
     */
    @Transactional
    public CommentReplyResponse saveCommentReply(SaveCommentReplyRequest request, Integer commentId, Integer userId) {
        // 유효성 체크1: 존재하는 코멘트인지 확인
        MovieComment movieComment = movieCommentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT));

        // 답글 개수 제한 X
//        // 유효성 체크3: 답글을 이미 등록했는지 확인
//        int count = movieCommentReplyRepository.countByMovieIdAndUserIdAndCommentId(movieId, userId, commentId);
//        if (count > 0) {
//            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_COMMENT_REPLY);
//        }

        // 해당 코멘트에 답글 count 반영 (db save)
        updateReplyCount(movieComment, 1);

        // 답글 저장
        CommentReply commentReply = movieCommentReplyRepository.save(request.toEntity(commentId, userId));

        return new CommentReplyResponse(commentReply);
    }

    /**
     * 영화 코멘트 답글 수정
     */
    @Transactional
    public CommentReplyResponse updateCommentReply(UpdateCommentReplyRequest request, Integer commentId, Integer replyId, Integer userId) {
        // 유효성 체크1: 존재하는 답글인지 확인
        CommentReply commentReply = movieCommentReplyRepository.findById(replyId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT_REPLY));

        // 코멘트 체크2: 작성자 확인
        if (!commentReply.getUserId().equals(userId)) {
            throw new RestApiException(CommonErrorCode.UNAUTHORIZED_ACTION);
        }

        // 코멘트 체크3: 코멘트 확인
        if (!commentReply.getCommentId().equals(commentId)) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST);
        }

        commentReply.setContent(request.getContent());
        commentReply = movieCommentReplyRepository.save(commentReply);

        return new CommentReplyResponse(commentReply);
    }

    /**
     * 영화 코멘트 답글 삭제
     */
    @Transactional
    public void deleteCommentReply(Integer commentId, Integer replyId, Integer userId) {
        // 유효성 체크1: 존재하는 답글인지 확인
        CommentReply commentReply = movieCommentReplyRepository.findById(replyId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT_REPLY));

        // 코멘트 체크2: 작성자 확인
        if (!commentReply.getUserId().equals(userId)) {
            throw new RestApiException(CommonErrorCode.UNAUTHORIZED_ACTION);
        }

        // 코멘트 체크3: 코멘트 확인
        if (!commentReply.getCommentId().equals(commentId)) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST);
        }

        // 유효성 체크4: 존재하는 코멘트인지 확인
        MovieComment movieComment = movieCommentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT));

        // 해당 코멘트에 답글 count 반영 (db save)
        updateReplyCount(movieComment, 1);

        // reply id 기준으로 삭제
        movieCommentReplyRepository.deleteById(replyId);
    }

    /**
     * 코멘트의 답글 수를 업데이트하는 메서드
     */
    private void updateReplyCount(MovieComment movieComment, int count) {
        movieComment.setLike(movieComment.getReplyCount() + count);
        movieCommentRepository.save(movieComment);
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * table : tb_movie_comment_summary
     */
    public Page<MovieCommentSummary> searchCommentSummary(Integer movieId, Pageable pageable) {
        return movieCommentSummaryRepository.findAllByMovieIdOrderByLike(movieId, pageable);
    }

    /**
     * 사용자가 평가한 코멘트 개수 조회
     *
     * @param userId
     * table : tb_movie_comment_summary
     */
    public int getUserCommentCount(Integer userId) {
        return movieCommentRepository.countAllByUserId(userId);
    }




























}
