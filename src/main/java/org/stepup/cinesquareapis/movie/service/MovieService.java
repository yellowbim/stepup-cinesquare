package org.stepup.cinesquareapis.movie.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieDetail;
import org.stepup.cinesquareapis.movie.repository.MovieDetailRepository;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<Integer> saveKoficMovie(int currentPage, int itemPerPage) {
        String movieListUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
        String movieDetailUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
        String key = "a440544b11856d33b630de4bf58546bb";

        ArrayList<Integer> createdMovieIds = new ArrayList<>();

        try {
            // 영화 목록 조회 API 호출
            String apiURL = movieListUrl + "?key=" + key;
            apiURL += currentPage > 0 ? "&curPage=" + currentPage : "";
            apiURL += itemPerPage > 0 ? "&itemPerPage=" + itemPerPage : "";
            URL url = new URL(apiURL);

            // API에서 응답 받은 JSON 문자열을 StringBuilder에 저장
            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    jsonString.append(line);
                }
            }

            // JSON 문자열을 JsonObject로 파싱
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);
            JsonArray koficMoviesJsonArray = jsonObject.getAsJsonObject("movieListResult").getAsJsonArray("movieList");

            // 영화 목록을 순회하며, 적재할 영화 선별 후 상세정보 api 호출
            for (JsonElement movieJsonElement : koficMoviesJsonArray) {
                jsonObject = movieJsonElement.getAsJsonObject();

                // 대표 장르(repGenreNm)가 없거나 기타로 분류 된 경우 데이터 적재를 하지 않음
                String genre = jsonObject.get("repGenreNm").getAsString();
                if (!genre.isEmpty() && !genre.equals("기타") && !genre.equals("성인물(에로)")) {

                    // 영화 상세 정보 API 호출
                    int movieCd = jsonObject.get("movieCd").getAsInt();

                    apiURL = movieDetailUrl + "?key=a440544b11856d33b630de4bf58546bb&movieCd=" + movieCd;
                    url = new URL(apiURL);

                    // API에서 응답 받은 JSON 문자열을 StringBuilder에 저장
                    jsonString = new StringBuilder();
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = bf.readLine()) != null) {
                            jsonString.append(line);
                        }
                    }

                    // JSON 문자열을 JsonObject로 파싱
                    gson = new Gson();
                    jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);
                    JsonObject movieInfo = jsonObject.getAsJsonObject("movieInfoResult").getAsJsonObject("movieInfo");

                    // 저장할 데이터 movie, movieDetail 객체로 생성
                    Movie movie = new Movie();
                    MovieDetail movieDetail = new MovieDetail();

                    // 필수 값: source, kofic_movie_code, genre
                    movieDetail.setSource(1);
                    movieDetail.setKoficMovieCode(movieCd);
                    movieDetail.setGenre(genre);

                    // genres
                    List<String> temps = new ArrayList<>();
                    for (JsonElement je : movieInfo.getAsJsonArray("genres")) {
                        JsonObject jo = je.getAsJsonObject();
                        temps.add(jo.get("genreNm").getAsString());
                    }
                    if (temps.size() > 0) {
                        movieDetail.setGenres(String.join(",", temps));
                    }

                    movieDetail.setMovieTitle(movieInfo.get("movieNm").getAsString());

                    movie.setMovieTitle(movieInfo.get("movieNm").getAsString());

                    String runningTime = movieInfo.get("showTm").getAsString();
                    if (runningTime != null && !runningTime.isEmpty()) {
                        movie.setRunningTime(Integer.parseInt(runningTime));
                        movieDetail.setRunningTime(Integer.parseInt(runningTime));
                    }

                    String movieTitleEn = movieInfo.get("movieNmEn").getAsString();
                    if (movieTitleEn != null && !movieTitleEn.isEmpty()) {
                        movieDetail.setMovieTitleEn(movieTitleEn);
                    }

                    String productionYear = movieInfo.get("prdtYear").getAsString();
                    if (productionYear != null && !productionYear.isEmpty()) {
                        movie.setProductionYear(Integer.parseInt(productionYear));
                        movieDetail.setProductionYear(Integer.parseInt(productionYear));
                    }

                    String openDate = movieInfo.get("openDt").getAsString();
                    if (openDate != null && !openDate.isEmpty()) {
                        try {
                            LocalDate parsedDate = LocalDate.parse(openDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                            movieDetail.setOpenDate(parsedDate);
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                        }
                    }

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

                    temps = new ArrayList<>();
                    for (JsonElement je : movieInfo.getAsJsonArray("directors")) {
                        JsonObject jo = je.getAsJsonObject();
                        temps.add(jo.get("peopleNm").getAsString());
                    }
                    if (temps.size() > 0) {
                        movieDetail.setDirector(temps.get(0));
                        movieDetail.setDirectors(String.join(",", temps));
                    }

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
                    MovieDetail createdMovieDetail = movieDetailRepository.save(movieDetail);

                    createdMovieIds.add((createdMovie.getMovieId()));
                }
            }

            System.out.println(createdMovieIds);

        } catch(Exception e) {
            e.printStackTrace();
        }

        return createdMovieIds;
    }

//    /**
//     * userId에 해당하는 User 조회
//     *
//     * @param userId
//     * @return
//     */
//    public UserResponse getUser(int userId) {
//        User user = movieRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//        return new UserResponse(user);
//    }
}
