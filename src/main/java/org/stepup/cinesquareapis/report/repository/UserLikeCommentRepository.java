package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;
import org.stepup.cinesquareapis.report.entity.UserLikeCommentKey;

import java.util.List;

@Repository
@Transactional
public interface UserLikeCommentRepository extends JpaRepository<UserLikeComment, UserLikeCommentKey> {
    // 코멘트 좋아요 등록 여부 확인
    boolean existsByUserIdAndMovieIdAndCommentId(Integer userId, Integer movieId, Integer commentId);

    // user_id, movie_id, commentId 기준으로 코멘트 좋아요 삭제
    int deleteByUserIdAndMovieIdAndCommentId(Integer userId, Integer movieId, Integer commentId);

    // commentId 기준으로 코멘트 좋아요 삭제
    int deleteByCommentId(Integer commentId);

    // 유저의 영화별 좋아요한 코멘트 id 목록 조회
    @Query("SELECT u.commentId FROM UserLikeComment u WHERE u.userId = :userId AND u.movieId = :movieId")
    List<Integer> findAllCommentIdsByUserIdAndMovieId(@Param("userId") Integer userId, @Param("movieId") Integer movieId);

    // 좋아요한 코멘트 개수 조회
    int countAllByUserId(Integer userId);
}
