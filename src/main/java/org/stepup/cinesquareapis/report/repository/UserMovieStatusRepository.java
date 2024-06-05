package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserMovieStatus;
import org.stepup.cinesquareapis.report.entity.UserMovieStatusKey;

@Repository
@Transactional
public interface UserMovieStatusRepository extends JpaRepository<UserMovieStatus, UserMovieStatusKey> {

    @Query("SELECT u.movieId FROM UserMovieStatus u WHERE u.userId = :userId")
    Page<Integer> findAllMovieIdsByUserId(@Param("userId") Integer userId, Pageable pageable);
}
