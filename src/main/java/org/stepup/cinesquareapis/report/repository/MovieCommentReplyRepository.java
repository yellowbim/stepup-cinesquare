package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;

import java.util.List;

@Repository
@Transactional
public interface MovieCommentReplyRepository extends JpaRepository<CommentReply, Integer> {
    // reply id 기준으로 조회
    CommentReply findByReplyId(Integer replyId);

    // reply id 기준으로 삭제
    int deleteByReplyId(Integer replyId);

    Boolean existsByReplyId(Integer replyId);

    List<CommentReply> findAllByCommentId(Integer commendId);
}
