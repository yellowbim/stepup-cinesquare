package org.stepup.cinesquareapis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.entity.Movie;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface MovieRepository extends JpaRepository<Movie, Integer> {
}