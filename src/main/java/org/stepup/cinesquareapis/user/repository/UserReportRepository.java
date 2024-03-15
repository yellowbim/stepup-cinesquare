package org.stepup.cinesquareapis.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.user.entity.UserReport;

import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, String> {

    // userId 만으로 정보 조회
    List<UserReport> findByUserId(String userId);

    // userId, movieId 조건으로 정보 조회
    List<UserReport> findByUserIdAndMovieId(String userId, String movieId);
}
