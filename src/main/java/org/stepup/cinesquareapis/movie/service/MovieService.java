package org.stepup.cinesquareapis.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.common.exception.enums.CommonErrorCode;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;
import org.stepup.cinesquareapis.movie.dto.MovieCategoryResponse;
import org.stepup.cinesquareapis.movie.dto.MovieDetailResponse;
import org.stepup.cinesquareapis.movie.dto.MovieRankResponse;
import org.stepup.cinesquareapis.movie.dto.MovieSimpleResponse;
import org.stepup.cinesquareapis.movie.repository.MovieBoxofficeRepository;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;
import org.stepup.cinesquareapis.movie.repository.MovieSimpleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieSimpleRepository movieSimpleRepository;
    private final MovieRepository movieRepository;
    private final MovieBoxofficeRepository movieBoxofficeRepository;

    /**
     * MovieSimple 조회
     *
     * @return new MovieResponse(movie)
     * @throws new RestApiException()
     */
    public MovieSimpleResponse getMovieSimple(int movieId) {
        MovieSimple movieSimple = movieSimpleRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SIMPLE));
        return new MovieSimpleResponse(movieSimple);
    }

    /**
     * Movie + MovieSimple 조회
     *
     * @return new MovieDetailResponse(movie, movieSimple)
     * @throws new RestApiException()
     */
    public MovieDetailResponse getMovieDetail(int movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SIMPLE));

        MovieSimple movieSimple = movieSimpleRepository.findById(movieId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE));

        return new MovieDetailResponse(movie, movieSimple);
    }

    /**
     * 주간 박스오피스 조회 (10개 이하)
     *
     * @return movies
     */
    public MovieRankResponse[] getMovieBoxoffices(String requestDate) {
        // 조회 요청 날짜를 LocalDate로 변환
        LocalDate requestLocalDate = null;
        try {
            requestLocalDate = LocalDate.parse(requestDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 1.  requestDate 가 미래 -> 오류
        if (requestLocalDate.isAfter(LocalDate.now())) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 2. 박스오피스의 마감일 계산
        LocalDate boxofficeClosingDate = calculateBoxofficeClosingDate(requestLocalDate);

        // 3. DB 조회
        MovieBoxoffice[] movieBoxoffices = movieBoxofficeRepository.findByEndDate(boxofficeClosingDate);

        // 조회된 데이터가 10개 초과 -> DB 오류
        if (movieBoxoffices.length > 10) {
            throw new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_BOXOFFICE);
        }

        // 4. 반환 데이터 생성
        MovieRankResponse[] movies = new MovieRankResponse[10];
        for (int i = 0; i < movies.length; i++) {
            MovieSimple movieSimple = movieSimpleRepository.findById(movieBoxoffices[i].getMovieId())
                    .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_SIMPLE));
            movies[i] = new MovieRankResponse(movieSimple,i+1);
        }

        return movies;
    }

    private LocalDate calculateBoxofficeClosingDate(LocalDate requestLocalDate) {
        boolean isMonday = requestLocalDate.getDayOfWeek() == DayOfWeek.MONDAY;

        if (isMonday && requestLocalDate.equals(LocalDate.now()) && LocalTime.now().isBefore(LocalTime.of(9, 1))) {
            return requestLocalDate.with(DayOfWeek.SUNDAY).minusWeeks(2);
        } else {
            System.out.println(requestLocalDate.with(DayOfWeek.SUNDAY).minusWeeks(1));
            return requestLocalDate.with(DayOfWeek.SUNDAY).minusWeeks(1);
        }
    }

    /**
     * 평균 별점 높은 영화 top10 조회
     *
     * @return top10Movies
     */
    public MovieRankResponse[] getCinesquareTop10() {
        MovieSimple[] getCinesquareTop10 = movieSimpleRepository.findTop10ByOrderByScoreDesc();
        MovieRankResponse[] top10Movies = new MovieRankResponse[10];
        for (int i = 0; i < getCinesquareTop10.length; i++) {

            top10Movies[i] = new MovieRankResponse(getCinesquareTop10[i], i + 1);
        }

        return top10Movies;
    }

    /**
     * 영화 제목으로 like 검색된 MovieSimple 목록 조회
     *
     * @return movies
     */
    public MovieSimpleResponse[] findMovie(String title) {
        String pattern = "^(?:[가-힣]{1,}|[a-zA-Z]{2,}|\\d{2,})$";
        if (!Pattern.matches(pattern, title)) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER, "movieTitle can't be searched :" + title);
        }

        List<MovieSimple> findMovies = movieSimpleRepository.findByTitleContaining(title);
        MovieSimpleResponse[] movies = new MovieSimpleResponse[findMovies.size()];
        for (int i = 0; i < findMovies.size(); i++) {
            movies[i] = new MovieSimpleResponse(findMovies.get(i));
        }

        return movies;
    }

    /**
     * 카테고리 조회
     * - tb_movie 테이블에서 geners 컬럼을 조회해서 distinct 결과 전달
     *
     * @return
     */
    public MovieCategoryResponse[] getCategoryList() {
        List<String> categoryList = movieRepository.findAllGenres();
        Set<String> categorySet = new HashSet<>();

        for (String genres : categoryList) {
            String[] genresArray = genres.split(",");
            for (String genre : genresArray) {
                categorySet.add(genre.trim());
            }
        }
        MovieCategoryResponse[] list = categorySet.stream().map(
                category -> {
                    MovieCategoryResponse response = new MovieCategoryResponse();
                    response.setCategory(category);
                    return response;
                }).toArray(MovieCategoryResponse[]::new);

        return list;
    }

    /**
     * 랜덤 영화 조회 (추후에 카테고리 조건이 추가되야함)
     * - 내가 평가한 영화 항목들은 제외되야함.
     *
     * @return movies
     */
    public Page<Movie> getRandomMovies(Integer userId, String category, Pageable pageable) {
        Page<Movie> movies;

        if (category == null || "".equals(category)) {
            movies =  movieRepository.findRandomMovie(userId, pageable);
        } else {
            movies =  movieRepository.findRandomMovieWithCategory(userId, category, pageable);
        }
        return movies;
    }
}