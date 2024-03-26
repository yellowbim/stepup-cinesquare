package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserStatus;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {
    Boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);

    @Query("SELECT tb_user_movie_status.status FROM UserStatus tb_user_movie_status WHERE tb_user_movie_status.userId = ?1 AND tb_user_movie_status.movieId = ?2")
    int findStatusByUserIdAndMovieIdi(Integer userId, Integer movieId);
}
