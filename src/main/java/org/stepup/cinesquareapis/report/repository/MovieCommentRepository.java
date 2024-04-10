package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;

@Repository
@Transactional
public interface MovieCommentRepository extends JpaRepository<Comment, Integer> {
    // commentId, movieId로 조회
    Comment findByCommentIdAndMovieId(Integer commentId, Integer movieId);

    // commentId 로 삭제
    int deleteByCommentId(Integer commentId);


    // key 값 있는지 조회
    Boolean existsByCommentId(Integer commentId);

//    void findByCommentId(Comment comment);
}
