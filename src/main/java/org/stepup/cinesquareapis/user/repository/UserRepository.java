package org.stepup.cinesquareapis.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.enums.RoleType;

import java.util.List;
import java.util.Optional;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByAccount(String account);

    @Modifying
    @Query("UPDATE User u set u.image = :filePath where u.userId = :userId")
    void updateImageByUserId(Integer userId, @Param("filePath") String filePath);

    @Query("SELECT a.image FROM User a WHERE a.userId = :userId")
    String findUserImage(Integer userId);

//    @Query("SELECT b.fileKey FROM User a INNER JOIN UploadInfo b ON a.image = b.fileId WHERE a.userId = :userId")
//    String findUserImage(Integer userId);

    Optional<User> findByAccount(String account);
    List<User> findAllByType(RoleType type);
}