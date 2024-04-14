package org.stepup.cinesquareapis.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.db.entity.LogMovieLoading;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface LogMovieLoadingRepository extends JpaRepository<LogMovieLoading, Integer> {
}