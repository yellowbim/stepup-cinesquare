package org.stepup.cinesquareapis.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.user.entity.User;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByAccount(String account);

    User findByAccount(String account);
}