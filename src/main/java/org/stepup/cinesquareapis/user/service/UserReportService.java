package org.stepup.cinesquareapis.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.user.entity.UserReport;
import org.stepup.cinesquareapis.user.entity.UserReportPk;
import org.stepup.cinesquareapis.user.repository.UserReportRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;

    /**
     * user별 영화 별점 부과
     *
     * @param userReport
     */
    public List<UserReport> searchMovieReport(String userId, String movieId) {
        if (movieId == null || "".equals(movieId)) {
            return userReportRepository.findByUserId(userId);
        } else {
            return userReportRepository.findByUserIdAndMovieId(userId, movieId);
        }
    }

    /**
     * user별 영화 별점 부과
     *
     * @param userReport
     */
    public void reportMovie(UserReport userReport) {
        userReportRepository.save(userReport);
    }
}
