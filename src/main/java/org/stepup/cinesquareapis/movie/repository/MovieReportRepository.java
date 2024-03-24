package org.stepup.cinesquareapis.movie.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.MovieReport;
import org.stepup.cinesquareapis.movie.model.ReadMovieReportResponse;

import java.util.List;

@Repository
@Transactional
public interface MovieReportRepository extends JpaRepository<MovieReport, Integer> {
    List<MovieReport> findAllByMovieId(Integer movieId);

}
