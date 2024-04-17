package org.stepup.cinesquareapis.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.model.*;
import org.stepup.cinesquareapis.report.repository.MovieCommentReplyRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentSummaryRepository;
import org.stepup.cinesquareapis.report.repository.UserLikeCommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieReportService {

    private final MovieCommentRepository movieCommentRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;
    private final MovieCommentReplyRepository movieCommentReplyRepository;
    private final MovieCommentSummaryRepository movieCommentSummaryRepository;

    /**
     * 영화 코멘트 작성
     */
    public Comment saveComment(MovieCommentSaveRequest request, Integer movieId) {
        // 값 존재 여부 판단
        int count = movieCommentRepository.countByMovieIdAndUserId(movieId, request.getUserId());
        if (count > 0) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_COMMENT);
        }

        return movieCommentRepository.save(request.toEntity(movieId));
    }

    /**
     * 영화 코멘트 수정
     */
    public Comment updateComment(MovieCommentUpdateRequest request, Integer movieId, Integer commentId) {
        // 데이터 조회
        Comment data = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);

        // 유효성 체크
        if (data.getCommentId() == null || "".equals(data.getCommentId())) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT);
        }

        data.setContent(request.getContent());
        data.setUserId(request.getUserId());
        data.setMovieId(movieId);
        return movieCommentRepository.save(data);
    }

    /**
     * 영화 코멘트 삭제
     */
    @Transactional
    public int deleteComment(Integer commentId) {
        // 코멘트 답변 삭제
        movieCommentReplyRepository.deleteByCommentId(commentId);
        // 코멘트 좋아요 삭제
        userLikeCommentRepository.deleteByCommentId(commentId);
        // 코멘트 삭제
        return movieCommentRepository.deleteByCommentId(commentId);
    }

    /**
     * 영화 코멘트 답글 작성
     */
    public CommentReply saveCommentReply(MovieCommentReplySaveRequest request, Integer commentId, Integer movieId) {
        // 실제 존재하는 코멘트인지 조회
        movieCommentRepository.findById(commentId).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT));

        // 이미 등록된 내용인지 조회
        int count = movieCommentReplyRepository.countByMovieIdAndUserIdAndCommentId(movieId, request.getUserId(), commentId);
        if (count > 0) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_COMMENT_REPLY);
        }

        return movieCommentReplyRepository.save(request.toEntity(commentId, movieId));
    }

    /**
     * 영화 코멘트 답글 수정
     */
    public CommentReply updateCommentReply(MovieCommentReplyUpdateRequest request, Integer commentId, Integer movieId, Integer replyId) {
        // reply id 기준으로 조회
        CommentReply data = movieCommentReplyRepository.findByReplyId(replyId);

        // 유효성 체크
        if (data.getReplyId() == null || "".equals(data.getReplyId())) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT_REPLY);
        }

        data.setContent(request.getContent());
        data.setUserId(request.getUserId());
        data.setCommentId(commentId);
        data.setMovieId(movieId);
        return movieCommentReplyRepository.save(data);
    }

    /**
     * 영화 코멘트 답글 삭제
     */
    public int deleteCommentReply(Integer commentId, Integer movieId, Integer replyId) {
        // reply id 기준으로 삭제
        return movieCommentReplyRepository.deleteByReplyId(replyId);
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * table : tb_movie_comment_summary
     */
    public List<MovieCommentSummaryResponse> searchCommentSummary(Integer movieId) {
        return movieCommentSummaryRepository.findAllByMovieId(movieId).stream().map(MovieCommentSummaryResponse::new).collect(Collectors.toList());
    }

    /**
     * 영화 코멘트 상세 및 답글 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    public List<MovieReplyResponse> searchReplyList(Integer commentId) {
        return movieCommentReplyRepository.findAllByCommentId(commentId).stream().map(MovieReplyResponse::new).collect(Collectors.toList());
    }

    /**
     * 사용자가 평가한 코멘트 개수 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    public int getUserCommentCount(Integer userId) {
        return movieCommentRepository.countAllByUserId(userId);
    }




























}
