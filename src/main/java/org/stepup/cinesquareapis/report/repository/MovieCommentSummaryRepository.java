package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.CommentSummary;
import org.stepup.cinesquareapis.report.model.MovieCommentSummaryResponse;

import java.util.List;

@Repository
@Transactional
public interface MovieCommentSummaryRepository extends JpaRepository<CommentSummary, Integer> {
    List<CommentSummary> findAllByCommentIdAndMovieIdAndUserId(Integer commentId, Integer movieId, Integer userId);
}
