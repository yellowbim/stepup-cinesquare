package org.stepup.cinesquareapis.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Repository
public interface DbSimpleRepository extends JpaRepository<MovieSimple, Integer> {
}