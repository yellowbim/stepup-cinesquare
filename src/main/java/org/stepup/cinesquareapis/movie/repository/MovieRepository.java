package org.stepup.cinesquareapis.movie.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.Movie;

import java.util.List;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE Movie m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);

    Movie findByKoficMovieCode(String koficMovieCode);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.synopsys = :synopsys WHERE m.movieId = :movieId")
    void updateSynopsys(String synopsys, Integer movieId);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.images = :images WHERE m.movieId = :movieId")
    void updateImages(String images, Integer movieId);

    @Query("SELECT m.images FROM Movie m WHERE m.movieId = :movieId")
    String findPosterUrlByMovieId(Integer movieId);

    @Query(value = "SELECT DISTINCT m.genres as genres FROM Movie m")
    List<String> findAllGenres();

    // 랜덤 영화 조회
    // native 쿼리 쓸때 마지막에 옵션 추가 필요!! + value 명시 필요!
    @Query(value = "SELECT * FROM cinesquare.tb_movie WHERE movie_id NOT IN (SELECT movie_id FROM cinesquare.tb_user_movie_score WHERE user_id = :userId) ORDER BY RAND()", nativeQuery = true)
    Page<Movie> findRandomMovie(Integer userId, Pageable pageable);

    // 카테고리 조건으로 랜덤 영화 조회
    @Query(value = "SELECT * FROM cinesquare.tb_movie WHERE movie_id NOT IN (SELECT movie_id FROM cinesquare.tb_user_movie_score WHERE user_id = :userId) AND genres LIKE %:category% ORDER BY RAND()", nativeQuery = true)
    Page<Movie> findRandomMovieWithCategory(Integer userId, String category, Pageable pageable);
}