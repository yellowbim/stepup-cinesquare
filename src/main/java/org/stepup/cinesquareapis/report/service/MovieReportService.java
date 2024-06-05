package org.stepup.cinesquareapis.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.report.dto.MovieCommentReplySaveRequest;
import org.stepup.cinesquareapis.report.dto.MovieCommentReplyUpdateRequest;
import org.stepup.cinesquareapis.report.dto.MovieCommentResponse;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
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
//        Comment data = new Comment();
        Comment comment = new Comment();
        MovieCommentResponse data = new MovieCommentResponse(comment);

        if (movieCommentRepository.existsByCommentIdAndMovieId(commentId, movieId)) {
            comment = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
            data = new MovieCommentResponse(comment);
        }

        return data;
    }

    /**
     * 영화 코멘트 답글 작성
     */
    public CommentReply saveCommentReply(MovieCommentReplySaveRequest request, Integer commentId, Integer movieId, Integer userId) {
        // 실제 존재하는 코멘트인지 조회
        movieCommentRepository.findById(commentId).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT));

        // 이미 등록된 내용인지 조회
        int count = movieCommentReplyRepository.countByMovieIdAndUserIdAndCommentId(movieId, userId, commentId);
        if (count > 0) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_COMMENT_REPLY);
        }

        // 영화 답글 수 추가
        // 해당 comment 에 좋아요 count 증가
        Comment comment = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
        comment.setReplyCount(comment.getReplyCount()+1);
        movieCommentRepository.save(comment);
        return movieCommentReplyRepository.save(request.toEntity(commentId, movieId, userId));
    }

    /**
     * 영화 코멘트 답글 수정
     */
    public CommentReply updateCommentReply(MovieCommentReplyUpdateRequest request, Integer commentId, Integer movieId, Integer replyId, Integer userId) {
        // reply id 기준으로 조회
        CommentReply data = movieCommentReplyRepository.findByReplyId(replyId);

        // 유효성 체크
        if (data.getReplyId() == null || "".equals(data.getReplyId())) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT_REPLY);
        }

        data.setContent(request.getContent());
        data.setUserId(userId);
        data.setCommentId(commentId);
        data.setMovieId(movieId);
        return movieCommentReplyRepository.save(data);
    }

    /**
     * 영화 코멘트 답글 삭제
     */
    public int deleteCommentReply(Integer commentId, Integer movieId, Integer replyId, Integer userId) {
        // 유저 ID 확인
        if (userId == null) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_USER); // 사용자 정보가 없으면 에러
        }

        // 영화 답글 수 감소
        // 해당 comment 에 좋아요 count 증가
        Comment comment = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
        comment.setReplyCount(comment.getReplyCount()-1);
        movieCommentRepository.save(comment);

        // reply id 기준으로 삭제
        return movieCommentReplyRepository.deleteByReplyId(replyId);
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * table : tb_movie_comment_summary
     */
    public Page<CommentSummary> searchCommentSummary(Integer movieId, Pageable pageable) {
        return movieCommentSummaryRepository.findAllByMovieIdOrderByLike(movieId, pageable);
    }

    /**
     * 영화 코멘트 상세 및 답글 조회
     *
     * @param comment_id
     * table : tb_movie_comment_summary
     */
    public Page<CommentReply> searchReplyList(Integer commentId, Pageable pageable) {
        return movieCommentReplyRepository.findAllByCommentId(commentId, pageable);
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
