package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.CommentSummary;

@Repository
@Transactional
public interface MovieCommentSummaryRepository extends JpaRepository<CommentSummary, Integer> {
    // movie_id 하나만으로 조회
//    List<CommentSummary> findAllByMovieId(Integer movieId);

    // movie_id 하나만으로 조회, 정렬조건 추가
    Page<CommentSummary> findAllByMovieIdOrderByLike(Integer movieId, Pageable pageable);

   // movie_id, comment_id 하나에 대하여 조회
   CommentSummary findByMovieIdAndCommentId(Integer movieId, Integer commentId);

    //영화별 유저가 좋아요한 코멘트 목록 조회
    @Query(value = "select A.* from cinesquare.v_movie_comment_summary A inner join cinesquare.tb_user_like_comment B on A.comment_id = B.comment_id where B.user_id = :userId", nativeQuery = true)
    Page<CommentSummary> findByLikeUserId(Integer userId, Pageable pageable);

}
