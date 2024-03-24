package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stepup.cinesquareapis.movie.service.MovieService;

import java.text.ParseException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Tag(name = "movies", description = "영화 관련 API")
@RequestMapping("api/movies")
@RestController
public class MovieController {

    private final MovieService movieService;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB), 100개씩만 호출하기
     *
     * @return
     * @throws ParseException
     */
    @Operation(
        summary = "한국영화진흥원 API 영화 생성 (최초 DB)"
    )
    @GetMapping("kofic")
    public ResponseEntity<ArrayList<Integer>> createKoficMovie(@RequestParam("current_page") int currentPage, @RequestParam("item_per_page") int itemPerPage, @RequestParam("start_production_year") int startProductionYear) {
        ArrayList<Integer> result = movieService.saveKoficMovie(currentPage, itemPerPage, startProductionYear);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}