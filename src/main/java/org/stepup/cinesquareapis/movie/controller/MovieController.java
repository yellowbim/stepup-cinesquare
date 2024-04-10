package org.stepup.cinesquareapis.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.common.model.DataResponse;
import org.stepup.cinesquareapis.common.model.ListResponse;
import org.stepup.cinesquareapis.movie.model.MovieDetailResponse;
import org.stepup.cinesquareapis.movie.model.MovieRankResponse;
import org.stepup.cinesquareapis.movie.model.MovieSimpleResponse;
import org.stepup.cinesquareapis.movie.service.MovieService;

@RequiredArgsConstructor
@Tag(name = "movies", description = "영화 관련 API")
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
    public ResponseEntity<ListResponse<MovieSimpleResponse[]>> getMovieSimple(@RequestParam(name = "title", required = true) String movieTitle) {
        MovieSimpleResponse[] list = movieService.findMovie(movieTitle);
        ListResponse<MovieSimpleResponse[]> response = new ListResponse<>();
        response.setList(list);

        return ResponseEntity.ok(response);
    }
}