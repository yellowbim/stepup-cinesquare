package org.stepup.cinesquareapis.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;

import java.time.LocalDate;

@Repository
public interface MovieBoxofficeRepository extends JpaRepository<MovieBoxoffice, Integer> {
    MovieBoxoffice[] findByEndDate(LocalDate endDate);
}