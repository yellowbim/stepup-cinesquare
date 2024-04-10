package org.stepup.cinesquareapis.db.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stepup.cinesquareapis.db.service.DbService;

import java.util.ArrayList;

@RequiredArgsConstructor
@Tag(name = "db", description = "DB 적재 관련 API")
@RequestMapping("db")
@RestController
public class DbController {

    private final DbService dbService;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB), 100개씩만 호출하기
     *
     * @return
     */
    @PostMapping("kofics")
    public ResponseEntity<ArrayList<Integer>> createKoficMovies(@RequestParam("current_page") int currentPage, @RequestParam("item_per_page") int itemPerPage, @RequestParam("start_production_year") int startProductionYear) {
        // 영화 기본 정보 저장
        ArrayList<Integer> createdMovieIds = dbService.saveKoficMovies(currentPage, itemPerPage, startProductionYear);

        return ResponseEntity.ok(createdMovieIds);
    }

    /**
     * 한국영화진흥원 API 영화 생성 (단 건)
     *
     * @return
     */
    @PostMapping("kofic/{kofic_movie_code}")
    public ResponseEntity<Integer> createKoficMovie(String koficMovieCode) {
        // 영화 기본 정보 저장
        int createdMovieIds = dbService.saveKoficMovie(koficMovieCode);

        return ResponseEntity.ok(createdMovieIds);
    }
}