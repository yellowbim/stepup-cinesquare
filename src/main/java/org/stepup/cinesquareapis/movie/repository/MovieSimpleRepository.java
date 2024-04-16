package org.stepup.cinesquareapis.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

import java.util.List;

@Repository
public interface MovieSimpleRepository extends JpaRepository<MovieSimple, Integer> {
    MovieSimple[] findTop10ByOrderByScoreDesc();

    List<MovieSimple>findByTitleContaining(String movieTitle);

    @Transactional
    @Modifying
    @Query("UPDATE MovieSimple m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);

    @Transactional
    @Modifying
    @Query("UPDATE MovieSimple m SET m.score = :score WHERE m.movieId = :movieId")
    int updateAvgScore(Double score, Integer movieId);
}