package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserScore;

import java.util.List;

@Repository
@Transactional
public interface UserScoreRepository extends JpaRepository<UserScore, Integer> {

}
