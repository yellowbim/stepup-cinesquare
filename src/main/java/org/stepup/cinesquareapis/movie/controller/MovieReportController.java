package org.stepup.cinesquareapis.movie.controller;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.movie.model.ReadMovieReportResponse;
import org.stepup.cinesquareapis.movie.model.SaveMovieReportRequest;
import org.stepup.cinesquareapis.movie.service.MovieReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/movies/report")
public class MovieReportController {

    private final MovieReportService movieReportService;

    /**
     * 영화 1, 사용자들 리뷰 조회
     */
    @GetMapping("{movieId}")
    public ResponseEntity<List<ReadMovieReportResponse>> readMovieUsersReport(@PathVariable("movieId") Integer movieId) {
        List<ReadMovieReportResponse> result = movieReportService.readMovieUsersReport(movieId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 영화 1, 사용자 리뷰 등록
     */
    @PostMapping("{movieId}/{userId}")
    public ResponseEntity<Boolean> saveMovieReport(@PathVariable("movieId") Integer movieId, @PathVariable("userId") Integer userId, @RequestBody SaveMovieReportRequest request) {
        movieReportService.saveMovieReport(movieId, userId, request);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
