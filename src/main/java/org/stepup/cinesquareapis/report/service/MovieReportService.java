package org.stepup.cinesquareapis.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.model.MovieCommentReplyRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentRequest;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;
import org.stepup.cinesquareapis.report.repository.MovieCommentReplyRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentRepository;
import org.stepup.cinesquareapis.report.repository.MovieCommentSummaryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieReportService {

    private final MovieCommentRepository movieCommentRepository;
    private final MovieCommentReplyRepository movieCommentReplyRepository;
    private final MovieCommentSummaryRepository movieCommentSummaryRepository;

    /**
     * 영화 1, 다중 사용자 리뷰 조회
     * return ReadMovieReportResponse
     */
//    public List<ReadMovieReportResponse> readMovieUsersReport(Integer movieId) {
//        List<UserScore> movieReports = movieReportRepository.findAllByMovieId(movieId); // 조회는 Repository에 있는 MovieReport Entity에서만 조회 가능
//
//        return movieReports.stream().map(ReadMovieReportResponse::new).collect(Collectors.toList()); // List를 받기 위해서 stream으로 처리를 해줘야함!
//    }

    /**
     * 영화 코멘트 작성
     */
    public void saveComment(MovieCommentRequest request) {
        // 수정
        if (movieCommentRepository.existsByCommentId(request.getCommentId())) {
            movieCommentRepository.findByCommentId(request.toEntity());
        } else {
            movieCommentRepository.save(request.toEntity());
        }
    }

    /**
     * 영화 코멘트 답글 작성
     */
    public void saveCommentReply(MovieCommentReplyRequest request) {
        movieCommentReplyRepository.save(request.toEntity());
    }

    /**
     * 영화 코멘트 + 점수 (api 호출용)
     *
     * table : tb_movie_comment_summary
     */
    public List<MovieCommentSummaryResponse> searchCommentSummary(Integer userId, Integer movieId, Integer commentId) {
        CommentSummary request = new CommentSummary();
        request.setCommentId(commentId);
        request.setMovieId(movieId);
        request.setUserId(userId);
        return movieCommentSummaryRepository.findAllByCommentIdAndMovieIdAndUserId(commentId, movieId, userId).stream().map(MovieCommentSummaryResponse::new).collect(Collectors.toList());
    }




























}
