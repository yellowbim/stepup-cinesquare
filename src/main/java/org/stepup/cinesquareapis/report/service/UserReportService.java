package org.stepup.cinesquareapis.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.report.model.UserLikeCommentRequest;
import org.stepup.cinesquareapis.report.model.UserScoreRequest;
import org.stepup.cinesquareapis.report.model.UserStatusRequest;
import org.stepup.cinesquareapis.report.repository.UserLikeCommentRepository;
import org.stepup.cinesquareapis.report.repository.UserScoreRepository;
import org.stepup.cinesquareapis.report.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserScoreRepository userScoreRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;

    /**
     * 유저별 영화 별점 부과
     */
    public boolean saveScore(UserScoreRequest request) {
        Boolean result = true;
        try {
            userScoreRepository.save(request.toEntity());
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 유저별 영화 상태 부과
     */
    public boolean saveStatus(UserStatusRequest request) {
        Boolean result = true;
        try {
            userStatusRepository.save(request.toEntity());
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 유저별 코멘트 좋아요 부과
     */
    public Boolean saveLikeComment(UserLikeCommentRequest request) {
        Boolean result = true;
        try {
            userLikeCommentRepository.save(request.toEntity());
        } catch (Exception e) {
            result = false;
        }
        return result;
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
     * 유저별 영화별 별점 조회
     *
     */
    public int searchMovieUserStatus(Integer userId, Integer movieId) {
        int result = 0;
        if (userStatusRepository.existsByUserIdAndMovieId(userId, movieId)) {
            result = userStatusRepository.findStatusByUserIdAndMovieIdi(userId, movieId);
        }
        return result;
    }

    /**
     * 유저별 좋아요한 코멘트 목록 조회
     *
     */
    public List<Integer> searchUserLikeCommentList(Integer userId) {
        List<Integer> result = new ArrayList<>();
        if (userLikeCommentRepository.existsByUserId(userId)) {
            result = userLikeCommentRepository.findCommentIdByUserId(userId);
        }
        return result;
    }























}
