package org.stepup.cinesquareapis.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

import java.util.List;

@Repository
public interface MovieSimpleRepository extends JpaRepository<MovieSimple, Integer> {
    MovieSimple[] findTop10ByOrderByScoreDesc();

    List<MovieSimple>findByTitleContaining(String movieTitle);

    @Transactional
    @Modifying
    @Query("UPDATE MovieSimple m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);

    @Transactional
    @Modifying
    @Query("UPDATE MovieSimple m SET m.score = :score WHERE m.movieId = :movieId")
    int updateAvgScore(Double score, Integer movieId);

    // 랜덤 영화 조회
    // native 쿼리 쓸때 마지막에 옵션 추가 필요!! + value 명시 필요!
    @Query(value = "SELECT A.* FROM cinesquare.tb_movie_simple A LEFT JOIN cinesquare.tb_user_movie_score B ON A.movie_id = B.movie_id WHERE B.movie_id IS NULL ORDER BY RAND()", nativeQuery = true)
    List<MovieSimple> findRandomMovie();

    // 카테고리 조건으로 랜덤 영화 조회
    @Query(value = "SELECT A.* FROM cinesquare.tb_movie_simple A LEFT JOIN cinesquare.tb_user_movie_score B ON A.movie_id = B.movie_id WHERE B.movie_id IS NULL AND A.genre = :category ORDER BY RAND()", nativeQuery = true)
    List<MovieSimple> findRandomMovieWithCategory(String category);
}