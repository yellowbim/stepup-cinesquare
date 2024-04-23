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
    // movie_id 하나만으로 조회
//    List<CommentSummary> findAllByMovieId(Integer movieId);

    // movie_id 하나만으로 조회, 정렬조건 추가
    List<CommentSummary> findAllByMovieIdOrderByLike(Integer movieId);

   // movie_id, comment_id 하나에 대하여 조회
   CommentSummary findByMovieIdAndCommentId(Integer movieId, Integer commentId);

}
