package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.movie.service.MovieDbLoadingService;

@RequiredArgsConstructor
@Tag(name = "db", description = "DB 적재 관련 API")
@RequestMapping("db-loading")
@RestController
public class MovieDbLoadingController {

    private final MovieDbLoadingService movieDbLoadingService;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB), 100개씩만 호출하기
     *
     * @return
     */
    @PostMapping("kofics")
    public void createKoficMovies(@RequestParam("current_page") int currentPage, @RequestParam("item_per_page") int itemPerPage, @RequestParam("start_production_year") int startProductionYear) {
        // 영화 기본 정보 저장
        int[] createdMovieIds = movieDbLoadingService.saveKoficMovies(currentPage, itemPerPage, startProductionYear);

        movieDbLoadingService.crawlAndDownloadImages(createdMovieIds);
    }

    /**
     * 한국영화진흥원 API 영화 생성 (단 건)
     *
     * @return
     */
    @PostMapping("kofic/{kofic_movie_code}")
    public void createKoficMovie(@PathVariable("kofic_movie_code") String koficMovieCode) {
        // 영화 기본 정보 저장
        int createdMovieId = movieDbLoadingService.saveKoficMovie(koficMovieCode);

        if (createdMovieId > 0) {
            int[] createdMovieIds = {createdMovieId};
            movieDbLoadingService.crawlAndDownloadImages(createdMovieIds);
        }
    }

    /**
     * CINE 영화 코드로 썸네일 다운로드
     *
     * @return
     */
    @PostMapping("cine/{movie_id}/thumbnail")
    public void createMoviePoster(int movieId) {
        int[] arr = {movieId};
        movieDbLoadingService.crawlAndDownloadImages(arr);
    }

    /**
     * KOFIC 영화 코드로 썸네일 다운로드
     *
     * @return
     */
    @PostMapping("kofic/{kofic_movie_code}/thumbnail")
    public void createMoviePoster(@RequestParam("kofic_movie_code") String koficMovieCode) {
        String[] arr = {koficMovieCode};
        movieDbLoadingService.crawlAndDownloadImages(arr);
    }
}