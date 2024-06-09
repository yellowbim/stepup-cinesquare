package org.stepup.cinesquareapis.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.MovieComment;

import java.util.Optional;

@Repository
public interface MovieCommentRepository extends JpaRepository<MovieComment, Integer> {
    // user_id, movie_id 로 값이 존재하는지 판단
    boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);

    // 유저별 코멘트 전체 조회
    @Query("SELECT mc FROM MovieComment mc WHERE mc.userId = :userId ORDER BY mc.updated DESC, mc.created DESC")
    Page<MovieComment> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
        
    // commentId, movieId로 조회
    Optional<MovieComment> findByCommentIdAndMovieId(Integer commentId, Integer movieId);

    // user_id, movie_id 로 조회
    MovieComment findByUserIdAndMovieId(Integer userId, Integer movieId);

    // 사용자가 평가한 코멘트 개수 조회
    int countAllByUserId(Integer userId);
}
