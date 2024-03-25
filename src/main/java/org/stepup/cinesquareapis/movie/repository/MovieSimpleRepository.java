package org.stepup.cinesquareapis.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Repository
public interface MovieSimpleRepository extends JpaRepository<MovieSimple, Integer> {
    MovieSimple[] findTop10ByOrderByScoreDesc();
}