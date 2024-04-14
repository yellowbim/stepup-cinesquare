package org.stepup.cinesquareapis.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

@Repository
public interface DbSimpleRepository extends JpaRepository<MovieSimple, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE MovieSimple m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);
}