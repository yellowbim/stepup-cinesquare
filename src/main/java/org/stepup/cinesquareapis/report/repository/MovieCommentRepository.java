package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

@Repository
@Transactional
public interface MovieCommentRepository extends JpaRepository<Comment, Integer> {
    int countByMovieIdAndUserId(Integer movieId, Integer userId);

    // commentId, movieId로 조회
    Comment findByCommentIdAndMovieId(Integer commentId, Integer movieId);

    // commentId, movieId로 값 존재 여부 조회
    Boolean existsByCommentIdAndMovieId(Integer commentId, Integer movieId);

    // user_id, movie_id 로 값이 존재하는지 판단
    Boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);
    // user_id, movie_id 로 조회
    Comment findByUserIdAndMovieId(Integer userId, Integer movieId);

    // commentId 로 삭제
    int deleteByCommentId(Integer commentId);

    // key 값 있는지 조회
    Boolean existsByCommentId(Integer commentId);

    // 사용자가 평가한 코멘트 개수 조회
    int countAllByUserId(Integer userId);
}
