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

    @Transactional
    @Modifying
    @Query("UPDATE Movie m SET m.thumbnail = true WHERE m.movieId = :movieId")
    void updateThumbnailToTrue(Integer movieId);

    Movie findByKoficMovieCode(String koficMovieCode);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.synopsys = :synopsys WHERE m.movieId = :movieId")
    void updateSynopsys(String synopsys, Integer movieId);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.imageIds = :imageIds WHERE m.movieId = :movieId")
    void updateImageIds(String imageIds, Integer movieId);

    @Query("SELECT m.imageIds FROM Movie m WHERE m.movieId = :movieId")
    String findPosterUrlByMovieId(Integer movieId);
}