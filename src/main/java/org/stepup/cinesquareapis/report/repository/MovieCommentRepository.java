package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.MovieComment;

import java.util.Optional;

@Repository
@Transactional
public interface MovieCommentRepository extends JpaRepository<MovieComment, Integer> {
    boolean existsByMovieIdAndUserId(Integer movieId, Integer userId);

    // commentId, movieId로 조회
    Optional<MovieComment> findByCommentIdAndMovieId(Integer commentId, Integer movieId);

    // user_id, movie_id 로 값이 존재하는지 판단
    Boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);
    // user_id, movie_id 로 조회
    MovieComment findByUserIdAndMovieId(Integer userId, Integer movieId);

    // 사용자가 평가한 코멘트 개수 조회
    int countAllByUserId(Integer userId);
}
