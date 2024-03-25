package org.stepup.cinesquareapis.report.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.report.entity.UserStatus;

@Repository
@Transactional
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {

}
