package org.stepup.cinesquareapis.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.movie.repository.MovieSimpleRepository;
import org.stepup.cinesquareapis.report.entity.*;
import org.stepup.cinesquareapis.report.model.MovieCommentResponse;
import org.stepup.cinesquareapis.report.model.MovieLikeCommentResponse;
import org.stepup.cinesquareapis.report.model.UserScoreRequest;
import org.stepup.cinesquareapis.report.repository.MovieCommentRepository;
import org.stepup.cinesquareapis.report.repository.UserLikeCommentRepository;
import org.stepup.cinesquareapis.report.repository.UserScoreRepository;
import org.stepup.cinesquareapis.report.repository.UserStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserScoreRepository userScoreRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;
    private final MovieCommentRepository movieCommentRepository;
    private final MovieSimpleRepository movieSimpleRepository;



    /**
     * 유저가 영화 1건에 남긴 코멘트 조회
     *
     */
    public MovieCommentResponse getMovieComment(Integer userId, Integer movieId) {
//        Comment data = new Comment();
        Comment comment = new Comment();
        MovieCommentResponse data = new MovieCommentResponse(comment);

        if (movieCommentRepository.existsByUserIdAndMovieId(userId, movieId)) {
            comment = movieCommentRepository.findByUserIdAndMovieId(userId, movieId);
            data = new MovieCommentResponse(comment);
        }

        return data;
    }

    /**
     * 유저별 영화별 별점 조회
     *
     */
    public Double searchMovieUserScore(Integer userId, Integer movieId) {
        Double result = 0.0;
        if (userScoreRepository.existsByUserIdAndMovieId(userId, movieId)) {
            result = userScoreRepository.findScoreByUserIdAndMovieId(userId, movieId);
        }
        return result;
    }

    /**
     * 유저별 영화 별점 부과
     */
    @Transactional
    public boolean saveScore(Integer userId, int movieId, UserScoreRequest request) {
        // score 범위 체크
        if (!isInValues(request.getScore())) {
            throw new RestApiException(CustomErrorCode.SCORE_RANGE_NOT_VALID);
        }

        // 해당 유저의 별점 확인
        Boolean checkUserData = userScoreRepository.existsByUserIdAndMovieId(userId, movieId);
        if (checkUserData) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_SCORE);
        }

        UserScore svaeUserScore = userScoreRepository.save(request.toEntity(userId, movieId));
        if (svaeUserScore.getScore() != null) {
            // 정상 처리인 경우 전체 평점 계산 로직 추가
            Double avgMovieScore = userScoreRepository.avgMovieScore(movieId);

            // 영화 기본정보에 update
            int updateCount = movieSimpleRepository.updateAvgScore(avgMovieScore, movieId);
            if (updateCount == 0) {
                throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
            }

            return true;
        }
        return false;
    }

    /**
     * 유저별 영화 별점 수정
     */
    @Transactional
    public boolean updateScore(Integer userId, int movieId, UserScoreRequest request) {
        // score 범위 체크
        if (!isInValues(request.getScore())) {
            throw new RestApiException(CustomErrorCode.SCORE_RANGE_NOT_VALID);
        }

        // key 셋팅
        UserScoreKey userScoreKey = new UserScoreKey(userId, movieId);
        // 해당 유저의 별점 확인
        UserScore userScore = userScoreRepository.findById(userScoreKey).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER_SCORE));
        userScore.setScore(request.getScore());

        userScoreRepository.save(userScore);
        if (userScore.getScore() != null) {
            // 정상 처리인 경우 전체 평점 계산 로직 추가
            Double avgMovieScore = userScoreRepository.avgMovieScore(movieId);

            // 영화 기본정보에 update
            int updateCount = movieSimpleRepository.updateAvgScore(avgMovieScore, movieId);
            if (updateCount == 0) {
                throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
            }
            return true;
        }
        return false;
    }

    /**
     * 유저별 영화 별점 삭제
     */
    @Transactional
    public void deleteScore(Integer userId, int movieId) {
        // 별점 삭제
        userScoreRepository.deleteByUserIdAndMovieId(userId, movieId);

        // 정상 처리인 경우 전체 평점 계산 로직 추가
        Double avgMovieScore = userScoreRepository.avgMovieScore(movieId);

        // 영화 기본정보에 update
        int updateCount = movieSimpleRepository.updateAvgScore(avgMovieScore, movieId);
        if (updateCount == 0) {
            throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
        }
    }

    /**
     * 영화별 사용자 상태 조회
     */
    @Transactional
    public Boolean getMovieUserStatus(Integer userId, Integer movieId) {
        // 기존 데이터 조회
        UserStatusKey userStatusKey = new UserStatusKey(userId, movieId);
        return userStatusRepository.existsById(userStatusKey);
    }

    /**
     * 유저별 영화 상태 저장
     */
    @Transactional
    public boolean saveStatus(Integer userId, Integer movieId) {
        // 기존 데이터 조회
        UserStatusKey userStatusKey = new UserStatusKey(userId, movieId);

        // 기존에 있는 상태인지 확인
        Optional<UserStatus> checkUserStatus = userStatusRepository.findById(userStatusKey);
        if (!checkUserStatus.isEmpty()) {
            throw new RestApiException(CustomErrorCode.ALERADY_REGISTED_USER_STATUS);
        }

        UserStatus userStatus = new UserStatus();
        userStatus.setUserId(userId);
        userStatus.setMovieId(movieId);

        UserStatus userStatusResult = userStatusRepository.save(userStatus);
        if (userStatusResult.getUserId() != null) {
            return true;
        }
        return false;
    }

    /**
     * 유저별 영화 상태 삭제
     */
    @Transactional
    public Boolean deleteStatus(Integer userId, int movieId) {
        // 기존 데이터 조회
        UserStatusKey userStatusKey = new UserStatusKey(userId, movieId);
        Optional<UserStatus> checkUserStatusData = userStatusRepository.findById(userStatusKey);
        if (checkUserStatusData.isEmpty()) {
            return false;
        } else {
            userStatusRepository.deleteById(userStatusKey);
            return true;
        }
    }

    /**
     * 유저별 좋아요한 코멘트 목록 조회
     *
     */
    @Transactional
    public List<MovieLikeCommentResponse> getUserLikeCommentList(Integer userId, Integer movieId) {
        // key setting
//        UserLikeCommentKey userLikeCommentKey = new UserLikeCommentKey(userId, movieId);

         return userLikeCommentRepository.findAllByUserIdAndMovieId(userId, movieId).stream().map(MovieLikeCommentResponse::new).collect(Collectors.toList());
    }

    /**
     * 유저별 코멘트 좋아요 등록
     */
    @Transactional
    public Boolean saveLikeComment(Integer userId, Integer movieId, Integer commentId) {
        // 실제 존재하는 코멘트인지 확인
        Comment data = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
        if (data == null) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_COMMENT);
        }

        // 기존에 좋아요를 누른 상태인지 확인
        Boolean userLikeCommentCount = userLikeCommentRepository.existsByUserIdAndMovieIdAndCommentId(userId, movieId, commentId);
        if (userLikeCommentCount) {
            throw new RestApiException(CustomErrorCode.ALERADY_REGISTED_USER_LIKE_COMMENTS);
        }

        UserLikeComment userLikeComment = new UserLikeComment();
        userLikeComment.setUserId(userId);
        userLikeComment.setMovieId(movieId);
        userLikeComment.setCommentId(commentId);

        UserLikeComment userLikeCommentsResult = userLikeCommentRepository.save(userLikeComment);

        // 해당 comment 에 좋아요 count 증가
        Comment comment = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
        comment.setLike(comment.getLike()+1);
        movieCommentRepository.save(comment);

        if (userLikeCommentsResult.getUserId() != null) {
            return true;
        }
        return false;
    }

    /**
     * 유저별 코멘트 좋아요 삭제
     */
    @Transactional
    public Boolean deleteLikeComment(Integer userId, Integer movieId, Integer commentId) {
        // 기존에 좋아요를 누른 상태인지 확인
        Boolean userLikeCommentCount = userLikeCommentRepository.existsByUserIdAndMovieIdAndCommentId(userId, movieId, commentId);
        if (!userLikeCommentCount) {
            return false;
        } else {
            userLikeCommentRepository.deleteByUserIdAndMovieIdAndCommentId(userId, movieId, commentId);

            // 해당 comment 에 좋아요 count 감소
            // 해당 comment 에 좋아요 count 증가
            Comment comment = movieCommentRepository.findByCommentIdAndMovieId(commentId, movieId);
            comment.setLike(comment.getLike()-1);
            movieCommentRepository.save(comment);
            return true;
        }
    }

    /**
     * 사용자가 부과한 별점 개수 조회
     */
    @Transactional
    public int getScoredCount(Integer userId) {
        return userScoreRepository.countByUserId(userId);
    }

    /**
     * 좋아요 한 코맨트 개수 조회
     */
    @Transactional
    public int getLikeCommentCounts(Integer userId) {
        return userLikeCommentRepository.countAllByUserId(userId);
    }

    /**
     * 평가한 영화 목록 조회(별점만)
     */
//    @Transactional
//    public List<UserScoredResponse> getScoredMovies(Integer userId) {
//        return userScoreRepository.findAllMoviesByUserId(userId);
//    }


    /**
     * score 범위 확인, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5
     *
     * @param score
     * @return true, false
     */
    private boolean isInValues(double value) {
        double[] values = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5};
        for (double v : values) {
            if (value == v) {
                return true;
            }
        }
        return false;
    }






















}
