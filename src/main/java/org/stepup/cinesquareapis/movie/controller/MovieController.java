package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.dto.DataResponse;
import org.stepup.cinesquareapis.common.dto.ListResponse;
import org.stepup.cinesquareapis.common.dto.PageResponse;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.dto.*;
import org.stepup.cinesquareapis.movie.service.MovieService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Tag(name = "3 movies", description = "영화 API")
@RequestMapping("api/movies")
@RestController
public class MovieController {

    private final MovieService movieService;

    /**
     * movie_id로 영화 상세 조회 (추후 캐싱)
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "movie_id로 영화 상세 조회")
    @GetMapping("{movie_id}")
    public ResponseEntity<DataResponse<MovieDetailResponse>> getMovieDetail(@PathVariable("movie_id") int movieId) {
        MovieDetailResponse data = movieService.getMovieDetail(movieId);
        DataResponse<MovieDetailResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * movie_id로 영화 단순 조회 (추후 캐싱)
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "movie_id로 영화 단순 조회")
    @GetMapping("{movie_id}/simple")
    public ResponseEntity<DataResponse<MovieSimpleResponse>> getMovieSimple(@PathVariable("movie_id") int movieId) {
        MovieSimpleResponse data = movieService.getMovieSimple(movieId);
        DataResponse<MovieSimpleResponse> response = new DataResponse<>();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    /**
     * 주간 박스오피스 top10 조회 (추후 캐싱)
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(
            summary = "주간 박스오피스 top10 조회",
            description = "* 박스오피스 데이터는 전 주 월~일 요일 기준\n" +
                    "* 요청 일자가 월요일인 경우, 오전 9시 1분(현재는 서버 시간)을 기준으로 박스오피스 변화"
    )
    @GetMapping("boxoffice")
    public ResponseEntity<ListResponse<MovieRankResponse[]>> getMovieBoxoffices(
            @Parameter(description = "yyyyMMdd 형식의 조회 요청 일자(오늘)") @RequestParam("request_date") String requestDate) {
        MovieRankResponse[] list = movieService.getMovieBoxoffices(requestDate);
        ListResponse<MovieRankResponse[]> response = new ListResponse<>();
        response.setList(list);

        return ResponseEntity.ok(response);
    }

    /**
     * 평균 별점 높은 영화 top10 조회 (추후 캐싱)
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "평균 별점 높은 영화 top10 조회")
    @GetMapping("cinesquare-ranking")
    public ResponseEntity<ListResponse<MovieRankResponse[]>> getCinesquareTop10() {
        MovieRankResponse[] list = movieService.getCinesquareTop10();
        ListResponse<MovieRankResponse[]> response = new ListResponse<>();
        response.setList(list);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 제목으로 영화 단순 조회
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "영화 제목으로 영화 단순 조회")
    @GetMapping("")
    public ResponseEntity<ListResponse<MovieSimpleResponse[]>> getMovieSimple(@RequestParam(name = "title", required = true) String title) {
        MovieSimpleResponse[] list = movieService.findMovie(title);
        ListResponse<MovieSimpleResponse[]> response = new ListResponse<>();
        response.setList(list);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 카테고리 리턴
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "영화 카테고리 목록 조회",
                description = "영화에 들어가있는 모든 카테고리 조회<br>" +
                                "한 영화에 '애니메이션,멜로/로맨스' 이렇게 들어가있는 경우 분리되어서 모든 카테고리를 리턴<br>" +
                                "현재 카테고리에 대하여 코드값으로 구분되어있지 않아서 한글 그 값 자체로 내려감")
    @GetMapping("movie-categories")
    public ResponseEntity<ListResponse<MovieCategoryResponse[]>> getCategoryList() {
        MovieCategoryResponse[] list = movieService.getCategoryList();
        ListResponse<MovieCategoryResponse[]> response = new ListResponse<>();
        response.setList(list);

        return ResponseEntity.ok(response);
    }

    /**
     * 평가하기 (랜덤 영화 조회)
     *
     * @return ResponseEntity.ok(response)
     */
    @Operation(summary = "평가하기 (페이징)",
                description = "- 카테고리를 선택하여 조회하는 경우 해당 **카테고리에 속한** 영화들만 응답됨<br>" +
                                "한 영화 하나에 '애니메이션,멜로/로맨스' 이런식으로 들어가있고, '멜로' 라는 단어를 카테고리로 넣어서 전달하면 포함된 문자열로 검색 ")
    @GetMapping("reports")
    public ResponseEntity<PageResponse<List<RandomMovieResponse>>> getRandomMovie(@AuthenticationPrincipal User principal, @RequestParam(name = "category", required = false) String category,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page, @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        Integer userId = Integer.parseInt(principal.getUsername());

        //페이지 셋팅
        Pageable pageable = PageRequest.of(page-1, size);

        Page<Movie> pagedData = movieService.getRandomMovies(userId, category, pageable);

        PageResponse<List<RandomMovieResponse>> response = new PageResponse<>(page, size);
        response.setList(pagedData.getContent().stream().map(RandomMovieResponse::new).collect(Collectors.toList())); // data
        response.setLastPage(pagedData.getTotalPages() == 0 ? 1: pagedData.getTotalPages()); // 마지막 페이지
        response.setTotalCount(pagedData.getTotalElements()); // 총 건수

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}