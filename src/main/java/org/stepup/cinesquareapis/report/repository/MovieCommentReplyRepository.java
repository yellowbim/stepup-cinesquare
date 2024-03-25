package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.Comment;
import org.stepup.cinesquareapis.report.entity.CommentReply;

@Repository
@Transactional
public interface MovieCommentReplyRepository extends JpaRepository<CommentReply, Integer> {
}
