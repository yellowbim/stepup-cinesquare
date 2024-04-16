package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserScore;
import org.stepup.cinesquareapis.report.entity.UserScoreKey;

import java.util.List;

@Repository
@Transactional
public interface UserScoreRepository extends JpaRepository<UserScore, UserScoreKey> {
    // 유저별 별점 삭제
    int deleteByUserIdAndMovieId(Integer userId, Integer movieId);

    // 영화별 평점 합산 조회
    @Query("SELECT SUM(score)/COUNT(score) FROM UserScore WHERE movieId = :movieId")
    Double avgMovieScore(Integer movieId);

    Boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);

    @Query("SELECT tb_user_movie_score.score FROM UserScore tb_user_movie_score WHERE tb_user_movie_score.userId = ?1 AND tb_user_movie_score.movieId = ?2")
    Double findScoreByUserIdAndMovieId(Integer userId, Integer movieId);
}
