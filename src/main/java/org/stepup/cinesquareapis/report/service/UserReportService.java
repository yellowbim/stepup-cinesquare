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
    public void saveScore(UserScoreRequest request) {
        userScoreRepository.save(request.toEntity());
    }

    /**
     * 유저별 영화 상태 부과
     */
    public void saveStatus(UserStatusRequest request) {
        userStatusRepository.save(request.toEntity());
    }

    /**
     * 유저별 코멘트 좋아요 부과
     */
    public void saveLikeComment(UserLikeCommentRequest request) {
        userLikeCommentRepository.save(request.toEntity());
    }
























}
