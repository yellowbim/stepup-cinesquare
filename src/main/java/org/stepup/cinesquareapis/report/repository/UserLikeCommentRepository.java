package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;
import org.stepup.cinesquareapis.report.entity.UserLikeCommentKey;
import org.stepup.cinesquareapis.report.entity.UserStatus;
import org.stepup.cinesquareapis.report.model.MovieLikeCommentResponse;

import java.util.List;

@Repository
@Transactional
public interface UserLikeCommentRepository extends JpaRepository<UserLikeComment, UserLikeCommentKey> {
    // 코멘트 좋아요 등록 여부 확인
    Boolean existsByUserIdAndMovieIdAndCommentId(Integer userId, Integer movieId, Integer commentId);

    // user_id, movie_id, commentId 기준으로 코멘트 좋아요 삭제
    int deleteByUserIdAndMovieIdAndCommentId(Integer userId, Integer movieId, Integer commentId);

    // commentId 기준으로 코멘트 좋아요 삭제
    int deleteByCommentId(Integer commentId);

    List<UserLikeComment> findAllByUserIdAndMovieId(Integer userId, Integer movieId);

    // 좋아요한 코멘트 개수 조회
    int countAllByUserId(Integer userId);
}
