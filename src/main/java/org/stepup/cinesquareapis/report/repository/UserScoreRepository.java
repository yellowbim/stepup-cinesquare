package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserScore;
import org.stepup.cinesquareapis.report.entity.UserScoreKey;
import org.stepup.cinesquareapis.report.model.UserMovieRating;
import org.stepup.cinesquareapis.report.model.UserScoredMovies;

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

    // 부과된 별점 개수
    int countByUserId(Integer userId);

    // 영화 별점 분포 (jpql 방식)
    // 이렇게 interface로 선언하려면 as 로 해당 컬럼 명을 한번 더 명시해줘야됨.
    @Query("select us.score as score, count(us.score) as count from UserScore us group by us.userId, us.score having us.userId = :userId")
    List<UserMovieRating> findUserMovieRating(Integer userId);

//    // 평가한 영화 목록 (AS 선언할때 언더스코어로 표기하면 안되고, 카멜케이스로 작성해야 인식됨!!!!)
    @Query(value = "select A.movie_id as movieId , A.score as score , B.title as title from cinesquare.tb_user_movie_score A inner join cinesquare.tb_movie B on A.movie_id = B.movie_id where A.user_id = :userId", nativeQuery = true)
    Page<UserScoredMovies> findMoviesAllByUserId(Integer userId, Pageable pageable);
}
