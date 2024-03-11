package org.stepup.cinesquareapis.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.user.entity.User;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface UserRepository extends JpaRepository<User, Integer> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 추가 가능
}
