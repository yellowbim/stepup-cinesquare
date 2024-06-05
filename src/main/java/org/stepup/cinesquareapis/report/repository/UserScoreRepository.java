package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserScore;
import org.stepup.cinesquareapis.report.entity.UserScoreKey;
import org.stepup.cinesquareapis.report.dto.UserMovieRating;
import org.stepup.cinesquareapis.report.dto.UserScoredMovies;

import java.util.List;

@Repository
@Transactional
public interface UserScoreRepository extends JpaRepository<UserScore, UserScoreKey> {
    // 유저별 영화 별점 삭제
    int deleteByUserIdAndMovieId(int userId, int movieId);

    boolean existsByUserIdAndMovieId(int userId, int movieId);

    // movieId와 userId로 UserScore를 조회하는 메서드
    @Query("SELECT u FROM UserScore u WHERE u.userId = :userId AND u.movieId = :movieId")
    UserScore findByUserIdAndMovieId(@Param("userId") int userId, @Param("movieId") int movieId);

    // 유저별 평가한 영화 개수
    int countByUserId(int userId);

    // 영화 별점 분포 (jpql 방식)
    // 이렇게 interface로 선언하려면 as 로 해당 컬럼 명을 한번 더 명시해줘야됨.
    @Query("select us.score as score, count(us.score) as count from UserScore us group by us.userId, us.score having us.userId = :userId")
    List<UserMovieRating> findUserMovieRating(Integer userId);

    // 평가한 영화 목록 (AS 선언할때 언더스코어로 표기하면 안되고, 카멜케이스로 작성해야 함)
    @Query(value = "select A.movie_id as movieId , A.score as score , B.title as title from cinesquare.tb_user_movie_score A inner join cinesquare.tb_movie B on A.movie_id = B.movie_id where A.user_id = :userId", nativeQuery = true)
    Page<UserScoredMovies> findMoviesAllByUserId(Integer userId, Pageable pageable);
}
