package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserLikeComment;
import org.stepup.cinesquareapis.report.entity.UserStatus;

import java.util.List;

@Repository
@Transactional
public interface UserLikeCommentRepository extends JpaRepository<UserLikeComment, Integer> {
    Boolean existsByUserId(Integer userId);

    @Query("SELECT ulc.commentId FROM UserLikeComment ulc WHERE ulc.userId = ?1")
    List<Integer> findCommentIdByUserId(Integer userId);
}
