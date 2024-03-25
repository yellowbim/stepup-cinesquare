package org.stepup.cinesquareapis.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.Movie;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie[] findTop10ByOrderByScoreDesc();
}