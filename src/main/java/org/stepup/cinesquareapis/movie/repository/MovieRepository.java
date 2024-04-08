package org.stepup.cinesquareapis.movie.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.movie.entity.Movie;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT m.posterIds FROM Movie m WHERE m.movieId = :movieId")
    String findPosterUrlByMovieId(Integer movieId);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.posterIds = :posterIds WHERE m.movieId = :movieId")
    void updatePosterIds(Integer movieId, String posterIds);
}