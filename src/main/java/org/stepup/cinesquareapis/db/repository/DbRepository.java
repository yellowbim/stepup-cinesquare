package org.stepup.cinesquareapis.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.Movie;

@Repository
// JpaRepository를 상속하여 사용. <객체, ID>
public interface DbRepository extends JpaRepository<Movie, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Movie m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);
}