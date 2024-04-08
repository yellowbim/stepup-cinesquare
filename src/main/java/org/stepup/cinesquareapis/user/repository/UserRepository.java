package org.stepup.cinesquareapis.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.user.entity.User;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByAccount(String account);

    User findByAccount(String account);

    @Modifying
    @Query("UPDATE User u set u.profile= :fileId where u.userId = :userId")
    void updateProfileByUserId(Integer userId, @Param("fileId") Integer fileId);

    // 사용자 profile 조회
    @Query("SELECT b.fileKey FROM User a INNER JOIN UploadInfo b ON a.profile=b.fileId WHERE a.userId = :userId")
    String findUserProfile(Integer userId);
}