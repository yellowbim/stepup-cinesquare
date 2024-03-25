package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.movie.model.MovieDetailResponse;
import org.stepup.cinesquareapis.movie.model.MovieRankResponse;
import org.stepup.cinesquareapis.movie.model.MovieSimpleResponse;
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
    @Operation(summary = "movie_id로 영화 단순 조회")
    @GetMapping("{movie_id}")
    public ResponseEntity<MovieSimpleResponse> getMovieSimple(@PathVariable("movie_id") int movieId) {
        MovieSimpleResponse result = movieService.getMovieSimple(movieId);

        return ResponseEntity.ok(result);
    }
    
    /**
     * movie_id로 영화 상세 조회 (추후 캐싱)
     *
     * @return
     */
    @Operation(summary = "movie_id로 영화 모든 상세 정보 조회")
    @GetMapping("{movie_id}/detail")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(@PathVariable("movie_id") int movieId) {
        MovieDetailResponse result = movieService.getMovieDetail(movieId);

        return ResponseEntity.ok(result);
    }

    /**
     * 주간 박스오피스 top10 조회 (추후 캐싱)
     *
     * @return
     */
    @Operation(summary = "주간 박스오피스 top10 조회 (오늘 날짜를 yyyyMMdd 형식으로 요청)")
    @GetMapping("boxoffice")
    public ResponseEntity<MovieRankResponse[]> getMovieBoxoffice(@RequestParam("end_date") String endDate) {
        MovieRankResponse[] result = movieService.getMovieBoxoffice(endDate);

        return ResponseEntity.ok(result);
    }

    /**
     * 평균 별점 높은 영화 top10 조회 (추후 캐싱)
     *
     * @return
     */
    @Operation(summary = "평균 별점 높은 영화 top10 조회")
    @GetMapping("cinesquare-rank")
    public ResponseEntity<MovieRankResponse[]> getCinesquareTop10() {
        MovieRankResponse[] result = movieService.getCinesquareTop10();

        return ResponseEntity.ok(result);
    }


    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB), 100개씩만 호출하기
     *
     * @return
     */
    @Operation(hidden = true)
    @PostMapping("kofic")
    public ResponseEntity<ArrayList<Integer>> createKoficMovie(@RequestParam("current_page") int currentPage, @RequestParam("item_per_page") int itemPerPage, @RequestParam("start_production_year") int startProductionYear) {
        ArrayList<Integer> result = movieService.saveKoficMovie(currentPage, itemPerPage, startProductionYear);

        return ResponseEntity.ok(result);
    }
}