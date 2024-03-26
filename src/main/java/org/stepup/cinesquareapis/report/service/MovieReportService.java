package org.stepup.cinesquareapis.report.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.model.MovieCommentReplyRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;
import org.stepup.cinesquareapis.report.model.MovieReplyResponse;
import org.stepup.cinesquareapis.report.repository.MovieCommentReplyRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentSummaryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieReportService {

    private final MovieCommentRepository movieCommentRepository;
    private final MovieCommentReplyRepository movieCommentReplyRepository;
    private final MovieCommentSummaryRepository movieCommentSummaryRepository;

    /**
     * 영화 코멘트 작성
     */
    public Boolean saveComment(MovieCommentRequest request) {
        Boolean result = false;
        // null => 생성, 13 등 숫자가 넘어옴 => 수정
        // 13 숫자가 넘어왔는데 잘못 넘어온 경우 => false
        if (request.getCommentId() == null || movieCommentRepository.existsByCommentId(request.getCommentId())) {
            movieCommentRepository.save(request.toEntity());
            result = true;
        }
        return result;
    }

    /**
     * 영화 코멘트 답글 작성
     */
    public Boolean saveCommentReply(MovieCommentReplyRequest request) {
        Boolean result = false;
        // null => 생성, 13 등 숫자가 넘어옴 => 수정
        // 13 숫자가 넘어왔는데 잘못 넘어온 경우 => false
        if (request.getReplyId() == null || movieCommentReplyRepository.existsByReplyId(request.getReplyId())) {
            movieCommentReplyRepository.save(request.toEntity());
            result = true;
        }
        return result;
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




























}
