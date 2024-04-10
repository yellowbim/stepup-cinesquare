package org.stepup.cinesquareapis.db.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.db.repository.DbRepository;
import org.stepup.cinesquareapis.db.repository.DbSimpleRepository;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbService {

    private final DbSimpleRepository dbSimpleRepository;
    private final DbRepository dbRepository;

    private static final String GENRE_ETC = "기타";
    private static final String GENRE_EROTIC = "성인물(에로)";
    private static final String GENRE_ROMANCE = "멜로/로맨스";
    private static final String ADULTS_ONLY = "청소년관람불가";

    private static final String KOFIC_KEY = "a440544b11856d33b630de4bf58546bb";
    private static final String KOFIC_MOVIE_LIST_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
    private static final String KOFIC_MOVIE_DETAIL_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB)
     * 기본정보 저장, 썸네일 저장
     * @return createdMovieIds
     */

    public ArrayList<Integer> saveKoficMovies(int currentPage, int itemPerPage, int startProductionYear) {
        // 영화 API 키 및 요청 URL 설정
        String movieListUrl = KOFIC_MOVIE_LIST_API_URL + "?key=" + KOFIC_KEY;

        // 코드 수정
        // ArrayList: 병렬 환경에서 안전하지 않을 수 있으며, 동시에 여러 스레드에서 접근하면 데이터 무결성 문제가 발생할 수 있음
        // Collections.synchronizedList: createdMovieIds 리스트의 스레드 안전성을 보장
        List<Integer> createdMovieIds = Collections.synchronizedList(new ArrayList<>());

        try {
            // 1. 영화 목록 조회 API URL 생성
            String apiUrl = buildMovieListApiUrl(movieListUrl, currentPage, itemPerPage, startProductionYear);

            // 2. 영화 목록 조회 API 호출 후 JSON 파싱
            JsonObject movieListJsonObject = fetchJsonFromUrl(apiUrl);
            JsonArray movieJsonArray = movieListJsonObject.getAsJsonObject("movieListResult").getAsJsonArray("movieList");

            // 3. 영화 데이터 저장 시 병렬 처리를 위한 Executor 생성
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<?>> futures = new ArrayList<>();

            // 4. 각 영화 별로 저장 프로세스 병렬로 진행
            for (JsonElement movieElement : movieJsonArray) {
                futures.add(executor.submit(() -> {
                    try {
                        JsonObject movieJsonObject = movieElement.getAsJsonObject();

                        // 대표 장르 유효성 체크
                        if (!isValidPrimaryGenre(movieJsonObject)) return;

                        String koficMovieCode = movieJsonObject.get("movieCd").getAsString();

                        saveMovie(koficMovieCode, createdMovieIds);
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

            executor.shutdown();
            if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                // Executor 서비스가 제 시간 안에 종료되지 않을 경우
                executor.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(createdMovieIds);
    }

    /**
     * 한국영화진흥원 API 영화 생성 (단 건)
     * 기본정보 저장, 썸네일 저장
     * @return createdMovieIds
     */
    public int saveKoficMovie(String koficMovieCode) {
        ArrayList<Integer> createdMovieIds = new ArrayList<Integer>();

        try {
            saveMovie(koficMovieCode, createdMovieIds);

            return createdMovieIds.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 영화 목록 호출 API URL 생성
    private String buildMovieListApiUrl(String baseUrl, int currentPage, int itemPerPage, int productionYear) {
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
    
    // API를 요청하여 결과를 JSON으로 파싱
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
    @Transactional
    protected void saveMovie(String koficMovieCode, List<Integer> createdMovieIds) throws IOException {
        // 1. 영화 목록 조회 API 호출 후 JSON 파싱
        String getMovieDetailUrl = KOFIC_MOVIE_DETAIL_API_URL + "?key=" + KOFIC_KEY + "&movieCd=" + koficMovieCode;

        JsonObject movieDetailJsonObject = fetchJsonFromUrl(getMovieDetailUrl);

        JsonObject movieInfo = movieDetailJsonObject.getAsJsonObject("movieInfoResult").getAsJsonObject("movieInfo");

        // 유효성 체크1
        if (!hasValidRunningTime(movieInfo)) return;

        // 유효성 체크2
        if (!hasValidGenreAndRating(movieInfo)) return;

        // 저장할 데이터 movie, movieDetail 객체로 생성
        MovieSimple movieSimple = new MovieSimple();
        Movie movie = new Movie();

        // 필수 값: genre, movie_title, running_time, production_year, source, kofic_movie_code
        movieSimple.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movieSimple.setRunningTime(movieInfo.get("showTm").getAsShort());
        movieSimple.setProductionYear(movieInfo.get("prdtYear").getAsShort());

        movie.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movie.setRunningTime(movieInfo.get("showTm").getAsShort());
        movie.setProductionYear(movieInfo.get("prdtYear").getAsShort());
        movie.setSource((short) 1);
        movie.setKoficMovieCode(koficMovieCode);


        // genres
        List<String> genres = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
            JsonObject genreObj = je.getAsJsonObject();
            String genre = genreObj.get("genreNm").getAsString();
            genres.add(genre);
        }
        movie.setGenre(genres.get(0));
        movie.setGenres(String.join(",", genres));

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
        List<String> nations = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("nations")) {
            JsonObject jo = je.getAsJsonObject();
            nations.add(jo.get("nationNm").getAsString());
        }

        if (nations.size() > 0) {
            movieSimple.setNation(nations.get(0));
            movie.setNation(nations.get(0));
            movie.setNations(String.join(",", nations));
        }

        // director, directors
        List<String> directors = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("directors")) {
            JsonObject jo = je.getAsJsonObject();
            directors.add(jo.get("peopleNm").getAsString());
        }
        if (directors.size() > 0) {
            movie.setDirector(directors.get(0));
            movie.setDirectors(String.join(",", directors));
        }

        // actors
        List<String> actors = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("actors")) {
            JsonObject jo = je.getAsJsonObject();
            actors.add(jo.get("peopleNm").getAsString());
        }
        if (actors.size() > 0) {
            movie.setActors(String.join(",", actors));
        }

        // tb_movie 저장
        Movie createdMovie = dbRepository.save(movie);

        // tb_movie_simple 저장
        movieSimple.setMovieId(createdMovie.getMovieId());
        dbSimpleRepository.save(movieSimple);

        createdMovieIds.add(movieSimple.getMovieId());
    }


    // 유효성 체크1
    // 영화 목록 조회에서 추출된 대표 장르 확인, '기타'가 아닌 장르여야 함
    private boolean isValidPrimaryGenre(JsonObject movieJsonObject) {
        String genre = movieJsonObject.get("repGenreNm").getAsString();

        if (genre == null) return false;
        if (genre.isEmpty()) return false;
        if (genre.equals(GENRE_ETC)) return false;

        return true;
    }

    // 유효성 체크2
    // 상영 시간
    private boolean hasValidRunningTime(JsonObject movieInfo) {
        String runningTime = movieInfo.get("showTm").getAsString();

        if (runningTime == null) return false;
        if (runningTime.isEmpty()) return false;

        return true;
    }

    // 유효성 체크3
    // 장르+시청 등급을 기준으로 확인
    private boolean hasValidGenreAndRating(JsonObject movieInfo) {
        for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
            JsonObject genreObj = je.getAsJsonObject();
            String genre = genreObj.get("genreNm").getAsString();
            if (genre.equals(GENRE_EROTIC)) return false;
            if (genre.equals(GENRE_ROMANCE) && hasAdultsOnlyRating(movieInfo)) return false;
        }

        return true;
    }

    private boolean hasAdultsOnlyRating(JsonObject movieInfo) {
        for (JsonElement je : movieInfo.getAsJsonArray("audits")) {
            JsonObject ratingObj = je.getAsJsonObject();
            if (ratingObj.get("watchGradeNm").getAsString().equals(ADULTS_ONLY)) {
                return true;
            }
        }

        return false;
    }
}
