package org.stepup.cinesquareapis.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stepup.cinesquareapis.auth.entity.UserRefreshToken;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Integer> {
    // userId면서 재발급 횟수가 count보다 작은 UserRefreshToken 객체를 반환
//    Optional<UserRefreshToken> findByUserIdAndReissueCountLessThan(Integer userId, long count);
}