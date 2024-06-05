package org.stepup.cinesquareapis.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.movie.repository.MovieSimpleRepository;
import org.stepup.cinesquareapis.report.entity.*;
import org.stepup.cinesquareapis.report.dto.*;
import org.stepup.cinesquareapis.report.repository.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportService {
    private final MovieSimpleRepository movieSimpleRepository;

    private final MovieSummaryRepository movieSummaryRepository;
    private final UserScoreRepository userScoreRepository;

    private final UserStatusRepository userStatusRepository;

    private final MovieCommentRepository movieCommentRepository;
    private final MovieCommentSummaryRepository movieCommentSummaryRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;

    /**
     * 유저별 영화 별점 조회
     */
    public UserMovieScoreResponse getUserMovieScore(int userId, int movieId) {
        UserScore userScore = userScoreRepository.findByUserIdAndMovieId(userId, movieId);
        if (userScore == null) {
            return new UserMovieScoreResponse(movieId);
        }

        return new UserMovieScoreResponse(userScore);
    }

    /**
     * 유저별 영화 별점 부과
     */
    @Transactional
    public UserMovieScoreResponse saveUserMovieScore(int userId, int movieId, UserScoreRequest request) {
        /// 유효성 체크1: score 범위 확인
        if (!isInValues(request.getScore())) {
            throw new RestApiException(CustomErrorCode.SCORE_RANGE_NOT_VALID);
        }

        // 유효성 체크2: 영화 존재 여부 확인
        MovieSummary movieSummary = movieSummaryRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SUMMARY));

        // 유효성 체크3: 해당 유저의 별점 확인
        boolean checkUserData = userScoreRepository.existsByUserIdAndMovieId(userId, movieId);
        if (checkUserData) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_SCORE);
        }


        // Data 업데이트1: 유저별 영화 별점 저장
        UserScore userScore = userScoreRepository.save(request.toEntity(userId, movieId));

        // Data 업데이트2: 영화별 별점 저장
        // 1) 전체 별점 부과 개수 +1
        movieSummary.setScoreCount(movieSummary.getScoreCount() + 1);

        // 2) 전체 별점 총합 반영
        if (movieSummary.getTotalScore() == null) {
            movieSummary.setTotalScore(request.getScore());
        } else {
            movieSummary.setTotalScore(movieSummary.getTotalScore() + request.getScore());
        }

        // 3) 각 별점 개수 +1
        updateMovieScoreCounts(movieSummary, request.getScore(), 1);

        movieSummaryRepository.save(movieSummary);

        // Data 업데이트3: 영화별 평점 계산&저장
        // 평점 계산 (소수점 아래 2번째 자리에서 반올림)
        float roundedAvgScore = Math.round((movieSummary.getTotalScore() / movieSummary.getScoreCount()) * 10) / 10.0f;
        int updateCount = movieSimpleRepository.updateAvgScore(roundedAvgScore, movieId);
        if (updateCount == 0) {
            log.error("Failed to update average score for movieId: {} with roundedAvgScore: {}", movieId, roundedAvgScore);
            throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
        }

        return new UserMovieScoreResponse(userScore);
    }

    /**
     * 유저별 영화 별점 수정
     */
    @Transactional
    public UserMovieScoreResponse updateScore(int userId, int movieId, UserScoreRequest request) {
        // 유효성 체크1: score 범위 확인
        if (!isInValues(request.getScore())) {
            throw new RestApiException(CustomErrorCode.SCORE_RANGE_NOT_VALID);
        }

        // 유효성 체크2: 영화 존재 여부 확인
        MovieSummary movieSummary = movieSummaryRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SUMMARY));

        // 유효성 체크3: 해당 유저의 별점 확인
        UserScoreKey userScoreKey = new UserScoreKey(userId, movieId); // key 셋팅
        UserScore userScore = userScoreRepository.findById(userScoreKey).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER_SCORE));

        // 유효성 체크4: 별점 변화 확인
        float newScore = request.getScore();
        float oldScore = userScore.getScore();

        if (newScore == oldScore) {
            throw new RestApiException(CustomErrorCode.ALREADY_REGISTED_SCORE);
        }


        // Data 업데이트1: 유저별 영화 별점 저장
        userScore.setScore(newScore);
        userScoreRepository.save(userScore);

        // Data 업데이트2: 영화별 별점 저장
        // 1) 전체 별점 부과 개수 -> 변화 없음

        // 2) 전체 별점 총합 반영
        float newTotalScore = movieSummary.getTotalScore() - oldScore + newScore;
        movieSummary.setTotalScore(newTotalScore);

        // 3) 각 별점 개수 +1, -1
        updateMovieScoreCounts(movieSummary, newScore, 1);
        updateMovieScoreCounts(movieSummary, oldScore, -1);

        movieSummaryRepository.save(movieSummary);

        // Data 업데이트3: 영화별 평점 계산&저장
        // 평점 계산 (소수점 아래 2번째 자리에서 반올림)
        float roundedAvgScore = Math.round((movieSummary.getTotalScore() / movieSummary.getScoreCount()) * 10) / 10.0f;

        int updateCount = movieSimpleRepository.updateAvgScore(roundedAvgScore, movieId);
        if (updateCount == 0) {
            log.error("Failed to update average score for movieId: {} with roundedAvgScore: {}", movieId, roundedAvgScore);
            throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
        }

        return new UserMovieScoreResponse(userScore);
    }

    /**
     * 유저별 영화 별점 삭제
     */
    @Transactional
    public void deleteScore(int userId, int movieId) {
        // 유효성 체크: 해당 유저의 별점 확인
        UserScoreKey userScoreKey = new UserScoreKey(userId, movieId); // key 셋팅
        UserScore userScore = userScoreRepository.findById(userScoreKey).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER_SCORE));

        // Data 업데이트1: 유저별 영화 별점 삭제
        userScoreRepository.deleteByUserIdAndMovieId(userId, movieId);

        // Data 업데이트2: 영화별 별점 저장
        MovieSummary movieSummary = movieSummaryRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SUMMARY));

        int oldScoreCount = movieSummary.getScoreCount();  // 현재 별점 부과 개수

        // 1) 전체 별점 부과 개수 -> -1
        movieSummary.setScoreCount(oldScoreCount - 1);

        // 2) 전체 별점 총합 반영
        if (oldScoreCount == 1) {
            movieSummary.setTotalScore(null); // 부과된 별점이 0개 일 땐, null
        } else {
            movieSummary.setTotalScore(movieSummary.getTotalScore() - userScore.getScore());
        }

        // 3) 각 별점 개수 계산 (삭제: -1)
        updateMovieScoreCounts(movieSummary, userScore.getScore(), -1);

        movieSummaryRepository.save(movieSummary);

        // Data 업데이트3: 영화별 평점 계산&저장
        // 평점 계산 (소수점 아래 2번째 자리에서 반올림)
        Float roundedAvgScore = null;
        if (oldScoreCount != 1) {
            roundedAvgScore = Math.round((movieSummary.getTotalScore() / movieSummary.getScoreCount()) * 10) / 10.0f;
        }

        int updateCount = movieSimpleRepository.updateAvgScore(roundedAvgScore, movieId);
        if (updateCount == 0) {
            log.error("Failed to update average score for movieId: {} with roundedAvgScore: {}", movieId, roundedAvgScore);
            throw new RestApiException(CustomErrorCode.MOVIE_DB_UPDATE_FAILED);
        }
    }

    /**
     * 유저별 영화 상태(보고싶어요) 조회
     */
    @Transactional
    public Boolean getMovieUserStatus(Integer userId, Integer movieId) {
        // 기존 데이터 조회
        UserStatusKey userStatusKey = new UserStatusKey(userId, movieId);
        return userStatusRepository.existsById(userStatusKey);
    }

    /**
     * 영화 상태(보고싶어요) 저장
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
     * 영화 상태(보고싶어요) 삭제
     */
    @Transactional
    public boolean deleteStatus(int userId, int movieId) {
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
     * 영화별 유저가 좋아요한 코멘트 목록 조회
     *
     */
    @Transactional
    public Page<UserLikeComment> getUserMovieLikeCommentList(Integer userId, Integer movieId, Pageable pageable) {
         return userLikeCommentRepository.findAllByUserIdAndMovieId(userId, movieId, pageable);
    }

    /**
     * 좋아요한 코멘트 목록 (페이징)
     *
     */
    @Transactional
    public Page<CommentSummary> getUserLikeComments(Integer userId, Pageable pageable) {
         return movieCommentSummaryRepository.findByLikeUserId(userId, pageable);
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
     * 유저별 평가한(별점 부과) 영화 개수 조회
     */
    @Transactional
    public int getScoredCount(int userId) {
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
     * 유저별 영화 별점 분포 조회 (mypage)
     */
    @Transactional
    public List<UserMovieRating> getUserMovieRating(int userId) {
        return userScoreRepository.findUserMovieRating(userId);
    }

    /**
     * 유저별 평가한(별점 부과) 영화 목록 조회
     */
    @Transactional
    public Page<UserScoredMovies> getScoredMovies(int userId, Pageable pageable) {
        return userScoreRepository.findMoviesAllByUserId(userId, pageable);
    }

    /**
     * score 범위 확인, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5
     *
     * @param value
     * @return true, false
     */
    private boolean isInValues(float value) {
        float[] values = {0.5F, 1, 1.5F, 2, 2.5F, 3, 3.5F, 4, 4.5F, 5};
        for (float v : values) {
            if (value == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * score 개수 가감
     *
     * @param movieSummary, score, delta
     */
    private void updateMovieScoreCounts(MovieSummary movieSummary, float score, int delta) {
        if (score == 0.5f) {
            movieSummary.setScoreCount_0_5(movieSummary.getScoreCount_0_5() + delta);
        } else if (score == 1.0f) {
            movieSummary.setScoreCount_1(movieSummary.getScoreCount_1() + delta);
        } else if (score == 1.5f) {
            movieSummary.setScoreCount_1_5(movieSummary.getScoreCount_1_5() + delta);
        } else if (score == 2.0f) {
            movieSummary.setScoreCount_2(movieSummary.getScoreCount_2() + delta);
        } else if (score == 2.5f) {
            movieSummary.setScoreCount_2_5(movieSummary.getScoreCount_2_5() + delta);
        } else if (score == 3.0f) {
            movieSummary.setScoreCount_3(movieSummary.getScoreCount_3() + delta);
        } else if (score == 3.5f) {
            movieSummary.setScoreCount_3_5(movieSummary.getScoreCount_3_5() + delta);
        } else if (score == 4.0f) {
            movieSummary.setScoreCount_4(movieSummary.getScoreCount_4() + delta);
        } else if (score == 4.5f) {
            movieSummary.setScoreCount_4_5(movieSummary.getScoreCount_4_5() + delta);
        } else if (score == 5.0f) {
            movieSummary.setScoreCount_5(movieSummary.getScoreCount_5() + delta);
        } else {
            throw new IllegalArgumentException("Invalid score: " + score);
        }
    }
}
