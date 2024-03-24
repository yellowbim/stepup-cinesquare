package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;
import org.stepup.cinesquareapis.movie.model.MovieAllResponse;
import org.stepup.cinesquareapis.movie.model.MovieResponse;
import org.stepup.cinesquareapis.movie.service.MovieService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Tag(name = "movies", description = "영화 관련 API")
@RequestMapping("api/movies")
@RestController
public class MovieController {

    private final MovieService movieService;

    /**
     * movie_id로 영화 조회
     *
     * @return
     */
    @Operation(summary = "movie_id로 영화  조회")
    @GetMapping("{movie_id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable("movie_id") int movieId) {
        MovieResponse result = movieService.getMovie(movieId);

        return ResponseEntity.ok(result);
    }
    
    /**
     * movie_id로 영화 상세 조회
     *
     * @return
     */
    @Operation(summary = "movie_id로 영화 모든 상세 정보 조회")
    @GetMapping("{movie_id}/all")
    public ResponseEntity<MovieAllResponse> getMovieDetail(@PathVariable("movie_id") int movieId) {
        MovieAllResponse result = movieService.getMovieAllInfo(movieId);

        return ResponseEntity.ok(result);
    }

    /**
     * 영화 주간 박스오피스 조회
     *
     * @return
     */
    @Operation(summary = "영화 주간 박스오피스 조회 (오늘 날짜를 yyyyMMdd 형식으로 요청)")
    @GetMapping("boxoffice")
    public ResponseEntity<MovieBoxoffice[]> getBoxoffice(@RequestParam("end_date") String endDate) {
        MovieBoxoffice[] result = movieService.getBoxoffice(endDate);

        return ResponseEntity.ok(result);
    }
    
    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB), 100개씩만 호출하기
     *
     * @return
     */
    @Operation(summary = "사용 X, 한국영화진흥원 API 영화 생성 (최초 DB)")
    @GetMapping("kofic")
    public ResponseEntity<ArrayList<Integer>> createKoficMovie(@RequestParam("current_page") int currentPage, @RequestParam("item_per_page") int itemPerPage, @RequestParam("start_production_year") int startProductionYear) {
        ArrayList<Integer> result = movieService.saveKoficMovie(currentPage, itemPerPage, startProductionYear);

        return ResponseEntity.ok(result);
    }
}