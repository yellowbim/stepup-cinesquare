package org.stepup.cinesquareapis.movie.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.common.exception.enums.CommonErrorCode;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;
import org.stepup.cinesquareapis.movie.model.MovieDetailResponse;
import org.stepup.cinesquareapis.movie.model.MovieRankResponse;
import org.stepup.cinesquareapis.movie.model.MovieSimpleResponse;
import org.stepup.cinesquareapis.movie.repository.MovieBoxofficeRepository;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;
import org.stepup.cinesquareapis.movie.repository.MovieSimpleRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
     * 주간 박스오피스 top10 조회
     *
     * @return
     */
    public MovieRankResponse[] getMovieBoxoffice(String today) {
        LocalDate todayLocalDate;

        try {
            // 현재 날짜, 시간, 요일
            todayLocalDate = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        LocalTime currentTime = LocalTime.now();
        DayOfWeek dayOfWeek = todayLocalDate.getDayOfWeek();

        // 전 주 일요일 날짜 계산
        LocalDate previousDate;
        switch (dayOfWeek) {
            case TUESDAY:
                previousDate = todayLocalDate.minusDays(2);
                break;
            case WEDNESDAY:
                previousDate = todayLocalDate.minusDays(3);
                break;
            case THURSDAY:
                previousDate = todayLocalDate.minusDays(4);
                break;
            case FRIDAY:
                previousDate = todayLocalDate.minusDays(5);
                break;
            case SATURDAY:
                previousDate = todayLocalDate.minusDays(6);
                break;
            case SUNDAY:
                previousDate = todayLocalDate.minusDays(7);
                break;
            default:
                // 현재 시간이 오전 9시 1분보다 빠른 경우
                if (currentTime.isBefore(LocalTime.of(9, 1))) {
                    previousDate = todayLocalDate.minusDays(8); // 박스오피스가 업데이트가 안돼서 2주전 데이터 사용
                } else {
                    previousDate = todayLocalDate.minusDays(1);
                }
                break;
        }

        MovieBoxoffice[] movieBoxoffices = movieBoxofficeRepository.findByEndDate(previousDate);

        if (movieBoxoffices.length != 10) {
            if (previousDate.isAfter(todayLocalDate)) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER, "Today is in the future: " + todayLocalDate);
            }

            throw new RestApiException(CustomErrorCode.NOT_FOUND_MOVIE_BOXOFFICE);
        }

        MovieRankResponse[] top10Movies = new MovieRankResponse[10];
        for (int i = 0; i < top10Movies.length; i++) {
            MovieBoxoffice movieBoxoffice = movieBoxoffices[i];
            top10Movies[i] = new MovieRankResponse(movieBoxoffice);
        }

        return top10Movies;
    }


    /**
     * 평균 별점 높은 영화 top10 조회
     *
     * @return
     */
    public MovieRankResponse[] getCinesquareTop10() {
        MovieSimple[] getCinesquareTop10 = movieSimpleRepository.findTop10ByOrderByScoreDesc();
        MovieRankResponse[] top10Movies = new MovieRankResponse[10];
        for (int i = 0; i < getCinesquareTop10.length; i++) {
            top10Movies[i] = new MovieRankResponse(getCinesquareTop10[i].getMovieId(), i + 1);
        }

        return top10Movies;
    }

    /**
     * 영화 제목으로 like 검색된 MovieSimple 목록 조회
     *
     * @return movies
     */
    public MovieSimpleResponse[] findMovie(String movieTitle) {
        String pattern = "^(?:[가-힣]{1,}|[a-zA-Z]{2,}|\\d{2,})$";
        if (!Pattern.matches(pattern, movieTitle)) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER, "movieTitle can't be searched :" + movieTitle);
        }

        List<MovieSimple> findMovies = movieSimpleRepository.findByMovieTitleContaining(movieTitle);
        MovieSimpleResponse[] movies = new MovieSimpleResponse[findMovies.size()];
        for (int i = 0; i < findMovies.size(); i++) {
            movies[i] = new MovieSimpleResponse(findMovies.get(i));
        }

        return movies;
    }

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB)
     *
     * @return createdMovieIds
     */

    @Transactional
    public ArrayList<Integer> saveKoficMovie(int currentPage, int itemPerPage, int startProductionYear) {
        // 영화 API 키 및 요청 URL 설정
        String key = "a440544b11856d33b630de4bf58546bb";
        String movieListUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=" + key;
        String movieDetailUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?key=" + key + "&movieCd=";

        ArrayList<Integer> createdMovieIds = new ArrayList<>();

        try {
            // 영화 목록 조회 API 호출 및 JSON 파싱
            String apiUrl = buildApiUrl(movieListUrl, currentPage, itemPerPage, startProductionYear);
            JsonObject movieListJsonObject = fetchJsonFromUrl(apiUrl);
            JsonArray movieJsonArray = movieListJsonObject.getAsJsonObject("movieListResult").getAsJsonArray("movieList");

            // 병렬 처리를 위한 Executor 생성
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<?>> futures = new ArrayList<>();

            // 각 영화별로 처리를 병렬로 진행
            for (JsonElement movieElement : movieJsonArray) {
                futures.add(executor.submit(() -> {
                    try {
                        JsonObject movieJsonObject = movieElement.getAsJsonObject();
                        saveMovie(movieJsonObject, movieDetailUrl, createdMovieIds);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            }

            // 모든 작업이 완료될 때까지 대기
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // Executor 종료
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createdMovieIds;
    }


    // 영화 목록 호출 api url 생성
    private String buildApiUrl(String baseUrl, int currentPage, int itemPerPage, int productionYear) {
        StringBuilder apiUrlBuilder = new StringBuilder(baseUrl);
        if (currentPage > 0) {
            apiUrlBuilder.append("&curPage=").append(currentPage);
        }
        if (itemPerPage > 0) {
            apiUrlBuilder.append("&itemPerPage=").append(itemPerPage);
        }
        if (productionYear > 0) {
            apiUrlBuilder.append("&prdtStartYear=").append(productionYear);
        }
        return apiUrlBuilder.toString();
    }

    private JsonObject fetchJsonFromUrl(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return JsonParser.parseString(jsonString.toString()).getAsJsonObject();
        }
    }

    // 올바른 영화인지 확인 후 영화 저장
    private void saveMovie(JsonObject movieJsonObject, String movieDetailUrl, ArrayList<Integer> createdMovieIds) throws IOException {
        // 유효성 체크1 - 기준: 대표 장르
        String genre = movieJsonObject.get("repGenreNm").getAsString();
        if (genre == null || genre.isEmpty() || genre.equals("기타")) {
            return;
        }

        // 영화 상세 정보 조회
        int movieCd = movieJsonObject.get("movieCd").getAsInt();
        JsonObject movieDetailJsonObject = fetchJsonFromUrl(movieDetailUrl + movieCd);
        JsonObject movieInfo = movieDetailJsonObject.getAsJsonObject("movieInfoResult").getAsJsonObject("movieInfo");

        // 유효성 체크2 - 기준: 시청시간
        String runningTime = movieInfo.get("showTm").getAsString();
        if (runningTime == null || runningTime.isEmpty()) {
            return;
        }
        // 유효성 체크3 - 기준: 장르 목록 기준, 국가+장르 목록+청소년관란불가
        List<String> temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.get("genreNm").getAsString().equals("성인물(에로)")) {
                return;
            }
            if (jo.get("genreNm").getAsString().equals("멜로/로맨스")) {
                for (JsonElement je2 : movieInfo.getAsJsonArray("audits")) {
                    JsonObject jo2 = je.getAsJsonObject();
                    if (jo2.get("watchGradeNm").getAsString().equals("청소년관람불가")) {
                        return;
                    }
                }
            }
            temps.add(jo.get("genreNm").getAsString());
        }

        // 저장할 데이터 movie, movieDetail 객체로 생성
        MovieSimple movieSimple = new MovieSimple();
        Movie movie = new Movie();

        // 필수 값: genre, movie_title, running_time, production_year, source, kofic_movie_code
        movieSimple.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movieSimple.setRunningTime(Integer.parseInt(runningTime));
        movieSimple.setProductionYear(movieInfo.get("prdtYear").getAsInt());

        movie.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movie.setRunningTime(Integer.parseInt(runningTime));
        movie.setProductionYear(movieInfo.get("prdtYear").getAsInt());
        movie.setGenre(genre);
        movie.setSource(1);
        movie.setKoficMovieCode(movieCd);

        // genres
        if (temps.size() > 0) {
            movie.setGenres(String.join(",", temps));
        }

        // movie_title_en
        String movieTitleEn = movieInfo.get("movieNmEn").getAsString();
        if (movieTitleEn != null && !movieTitleEn.isEmpty()) {
            movie.setMovieTitleEn(movieTitleEn);
        }

        // open_date
        String openDate = movieInfo.get("openDt").getAsString();
        if (openDate != null && !openDate.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(openDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                movie.setOpenDate(parsedDate);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        // nation, nations
        temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("nations")) {
            JsonObject jo = je.getAsJsonObject();
            temps.add(jo.get("nationNm").getAsString());
        }
        if (temps.size() > 0) {
            movieSimple.setNation(temps.get(0));
            movie.setNation(temps.get(0));
            movie.setNations(String.join(",", temps));
        }

        // director, directors
        temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("directors")) {
            JsonObject jo = je.getAsJsonObject();
            temps.add(jo.get("peopleNm").getAsString());
        }
        if (temps.size() > 0) {
            movie.setDirector(temps.get(0));
            movie.setDirectors(String.join(",", temps));
        }

        // actors
        temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("actors")) {
            JsonObject jo = je.getAsJsonObject();
            temps.add(jo.get("peopleNm").getAsString());
        }
        if (temps.size() > 0) {
            movie.setActors(String.join(",", temps));
        }

        // tb_movie 저장
        Movie createdMovie = movieRepository.save(movie);

        // tb_movie_simple 저장
        movieSimple.setMovieId(createdMovie.getMovieId());
        movieSimpleRepository.save(movieSimple);

        createdMovieIds.add(movieSimple.getMovieId());
    }
}
