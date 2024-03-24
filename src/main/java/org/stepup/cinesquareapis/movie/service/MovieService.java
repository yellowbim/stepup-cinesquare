package org.stepup.cinesquareapis.movie.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieDetail;
import org.stepup.cinesquareapis.movie.repository.MovieDetailRepository;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieDetailRepository movieDetailRepository;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB)
     *
     * @return createdMovieId
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
        // 유효성 체크1 (대표 장르 기준)
        String genre = movieJsonObject.get("repGenreNm").getAsString();
        if (genre == null || genre.isEmpty() || genre.equals("기타")) {
            return;
        }

        // 영화 상세 정보 조회
        int movieCd = movieJsonObject.get("movieCd").getAsInt();
        JsonObject movieDetailJsonObject = fetchJsonFromUrl(movieDetailUrl + movieCd);
        JsonObject movieInfo = movieDetailJsonObject.getAsJsonObject("movieInfoResult").getAsJsonObject("movieInfo");

        // 유효성 체크2 (시청시간 기준)
        String runningTime = movieInfo.get("showTm").getAsString();
        if (runningTime == null || runningTime.isEmpty()) {
            return;
        }
        // 유효성 체크3 (장르 목록 기준), 장르 목록 추출
        List<String> temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.get("genreNm").getAsString().equals("성인물(에로)")) {
                return;
            }
            temps.add(jo.get("genreNm").getAsString());
        }

        // 저장할 데이터 movie, movieDetail 객체로 생성
        Movie movie = new Movie();
        MovieDetail movieDetail = new MovieDetail();

        // 필수 값: genre, movie_title, running_time, production_year, source, kofic_movie_code
        movie.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movie.setRunningTime(Integer.parseInt(runningTime));
        movie.setProductionYear(movieInfo.get("prdtYear").getAsInt());

        movieDetail.setMovieTitle(movieInfo.get("movieNm").getAsString());
        movieDetail.setRunningTime(Integer.parseInt(runningTime));
        movieDetail.setProductionYear(movieInfo.get("prdtYear").getAsInt());
        movieDetail.setGenre(genre);
        movieDetail.setSource(1);
        movieDetail.setKoficMovieCode(movieCd);

        // genres
        if (temps.size() > 0) {
            movieDetail.setGenres(String.join(",", temps));
        }

        // movie_title_en
        String movieTitleEn = movieInfo.get("movieNmEn").getAsString();
        if (movieTitleEn != null && !movieTitleEn.isEmpty()) {
            movieDetail.setMovieTitleEn(movieTitleEn);
        }

        // open_date
        String openDate = movieInfo.get("openDt").getAsString();
        if (openDate != null && !openDate.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(openDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                movieDetail.setOpenDate(parsedDate);
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
            movie.setNation(temps.get(0));
            movieDetail.setNation(temps.get(0));
            movieDetail.setNations(String.join(",", temps));
        }

        // director, directors
        temps = new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("directors")) {
            JsonObject jo = je.getAsJsonObject();
            temps.add(jo.get("peopleNm").getAsString());
        }
        if (temps.size() > 0) {
            movieDetail.setDirector(temps.get(0));
            movieDetail.setDirectors(String.join(",", temps));
        }

        // actors
        temps= new ArrayList<>();
        for (JsonElement je : movieInfo.getAsJsonArray("actors")) {
            JsonObject jo = je.getAsJsonObject();
            temps.add(jo.get("peopleNm").getAsString());
        }
        if (temps.size() > 0) {
            movieDetail.setActors(String.join(",", temps));
        }

        // tb_movie 저장
        Movie createdMovie = movieRepository.save(movie);

        // tb_movie_detail 저장
        movieDetail.setMovieId(createdMovie.getMovieId());
        movieDetailRepository.save(movieDetail);

        createdMovieIds.add(createdMovie.getMovieId());
    }

//    @Transactional
//    public ArrayList<Integer> saveKoficMovie(int currentPage, int itemPerPage, int productionYear) {
//        String key = "a440544b11856d33b630de4bf58546bb";
//        String movieListUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=" + key;
//        String movieDetailUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?key=" + key + "movieCd=";
//
//        ArrayList<Integer> createdMovieIds = new ArrayList<>();
//
//        try {
//            // 영화 목록 조회 API 호출 및 JSON 파싱
//            String apiUrl = buildApiUrl(movieListUrl, currentPage, itemPerPage, productionYear);
//            JsonObject movieListJsonObject = fetchJsonFromUrl(apiUrl);
//            JsonArray movieJsonArray = movieListJsonObject.getAsJsonObject("movieListResult").getAsJsonArray("movieList");
//
//            // 영화 목록을 순회하며, 적재할 영화 선별 후 상세정보 api 호출
//            for (JsonElement movieJsonElement : movieJsonArray) {
//                jsonObject = movieJsonElement.getAsJsonObject();
//
//                // 대표 장르(repGenreNm)가 없거나 기타로 분류 된 경우 데이터 적재를 하지 않음
//                String runningTime = jsonObject.get("showTm").getAsString();
//                if (runningTime == null || runningTime.isEmpty()) {
//                    continue;
//                }
//
//                String genre = jsonObject.get("repGenreNm").getAsString();
//                if (genre !=null && !genre.isEmpty() && !genre.equals("기타") && !genre.equals("성인물(에로)")) {
//                    continue;
//                }
//
//                // 영화 상세 정보 API 호출
//                int movieCd = jsonObject.get("movieCd").getAsInt();
//                apiURL = movieDetailUrl + movieCd;
//                url = new URL(apiURL);
//
//                // API에서 응답 받은 JSON 문자열을 StringBuilder에 저장
//                jsonString = new StringBuilder();
//                try (BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
//                    String line;
//                    while ((line = bf.readLine()) != null) {
//                        jsonString.append(line);
//                    }
//                }
//
//                // JSON 문자열을 JsonObject로 파싱
//                gson = new Gson();
//                jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);
//                JsonObject movieInfo = jsonObject.getAsJsonObject("movieInfoResult").getAsJsonObject("movieInfo");
//
//                // 저장할 데이터 movie, movieDetail 객체로 생성
//                Movie movie = new Movie();
//                MovieDetail movieDetail = new MovieDetail();
//
//                // 필수 값: source, kofic_movie_code, genre
//                movieDetail.setSource(1);
//                movieDetail.setKoficMovieCode(movieCd);
//                movieDetail.setGenre(genre);
//
//                // genres
//                List<String> temps = new ArrayList<>();
//                for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
//                    JsonObject jo = je.getAsJsonObject();
//                    temps.add(jo.get("genreNm").getAsString());
//                }
//                if (temps.size() > 0) {
//                    movieDetail.setGenres(String.join(",", temps));
//                }
//
//                movieDetail.setMovieTitle(movieInfo.get("movieNm").getAsString());
//
//                movie.setMovieTitle(movieInfo.get("movieNm").getAsString());
//
//                movie.setRunningTime(Integer.parseInt(runningTime));
//                movieDetail.setRunningTime(Integer.parseInt(runningTime));
//
//                String movieTitleEn = movieInfo.get("movieNmEn").getAsString();
//                if (movieTitleEn != null && !movieTitleEn.isEmpty()) {
//                    movieDetail.setMovieTitleEn(movieTitleEn);
//                }
//
//                String productionYear = movieInfo.get("prdtYear").getAsString();
//                if (productionYear != null && !productionYear.isEmpty()) {
//                    movie.setProductionYear(Integer.parseInt(productionYear));
//                    movieDetail.setProductionYear(Integer.parseInt(productionYear));
//                }
//
//                String openDate = movieInfo.get("openDt").getAsString();
//                if (openDate != null && !openDate.isEmpty()) {
//                    try {
//                        LocalDate parsedDate = LocalDate.parse(openDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
//                        movieDetail.setOpenDate(parsedDate);
//                    } catch (DateTimeParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                temps = new ArrayList<>();
//                for (JsonElement je : movieInfo.getAsJsonArray("nations")) {
//                    JsonObject jo = je.getAsJsonObject();
//                    temps.add(jo.get("nationNm").getAsString());
//                }
//                if (temps.size() > 0) {
//                    movie.setNation(temps.get(0));
//                    movieDetail.setNation(temps.get(0));
//                    movieDetail.setNations(String.join(",", temps));
//                }
//
//                temps = new ArrayList<>();
//                for (JsonElement je : movieInfo.getAsJsonArray("directors")) {
//                    JsonObject jo = je.getAsJsonObject();
//                    temps.add(jo.get("peopleNm").getAsString());
//                }
//                if (temps.size() > 0) {
//                    movieDetail.setDirector(temps.get(0));
//                    movieDetail.setDirectors(String.join(",", temps));
//                }
//
//                temps= new ArrayList<>();
//                for (JsonElement je : movieInfo.getAsJsonArray("actors")) {
//                    JsonObject jo = je.getAsJsonObject();
//                    temps.add(jo.get("peopleNm").getAsString());
//                }
//                if (temps.size() > 0) {
//                    movieDetail.setActors(String.join(",", temps));
//                }
//
//                // tb_movie 저장
//                Movie createdMovie = movieRepository.save(movie);
//
//                // tb_movie_detail 저장
//                movieDetail.setMovieId(createdMovie.getMovieId());
//                movieDetailRepository.save(movieDetail);
//
//                createdMovieIds.add((createdMovie.getMovieId()));
//            }
//
//            System.out.println(createdMovieIds);
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//
//        return createdMovieIds;
//    }


}
