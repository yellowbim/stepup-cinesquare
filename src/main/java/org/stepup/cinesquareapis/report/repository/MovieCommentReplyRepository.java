package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.CommentReply;

@Repository
@Transactional
public interface MovieCommentReplyRepository extends JpaRepository<CommentReply, Integer> {
//    int countByMovieIdAndUserIdAndCommentId(Integer movieId, Integer userId, Integer commentId);

    // comment id 기준으로 삭제
    int deleteByCommentId(Integer commentId);

    Page<CommentReply> findAllByCommentId(Integer commendId, Pageable pageable);
}
