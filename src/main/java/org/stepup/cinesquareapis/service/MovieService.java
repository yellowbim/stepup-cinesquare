package org.stepup.cinesquareapis.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stepup.cinesquareapis.entity.MovieDetail;
import org.stepup.cinesquareapis.repository.MovieRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB)
     *
     * @return createdMovieId
     */
    @Transactional
    public ArrayList<Integer> createKoficMovie(int currentPage, int itemPerPage) {
        String key = "a440544b11856d33b630de4bf58546bb";

        ArrayList<Integer> createdMovieIds = new ArrayList<>();

        try {
            String apiURL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=" + key;
            if (currentPage > 0) {
                apiURL += "&curPage=" + currentPage;
            }
            if (itemPerPage > 0) {
                apiURL += "&itemPerPage=" + itemPerPage;
            }
            URL url = new URL(apiURL);

            // API에서 응답 받은 JSON 문자열을 StringBuilder에 저장
            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    jsonString.append(line);
                }
            }

            // Gson을 사용하여 JSON 문자열을 JsonObject로 파싱
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);

            JsonArray koficMovies = jsonObject.getAsJsonObject("movieListResult").getAsJsonArray("movieList");


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

            for (JsonElement je : koficMovies) {
                JsonObject jo = je.getAsJsonObject();

                // 대표 장르(repGenreNm)가 없거나 기타로 분류 된 경우 데이터 적재를 하지 않음
                String genre = jo.get("repGenreNm").getAsString();
                if (!genre.isEmpty() && !genre.equals("기타")) {
                    MovieDetail movie = new MovieDetail();

                    movie.setGenre(genre);
                    movie.setSource(1);
                    movie.setKoficMovieCode(jo.get("movieCd").getAsInt());
                    movie.setMovieTitle(jo.get("movieNm").getAsString());

                    String movieTitleEn = jo.get("movieNmEn").getAsString();
                    if (movieTitleEn != null && !movieTitleEn.isEmpty()) {
                        movie.setMovieTitleEn(movieTitleEn);
                    }

                    String genreList = jo.get("genreAlt").getAsString();
                    if (genreList != null && !genreList.isEmpty()) {
                        movie.setGenreList(genreList);
                    }

                    String productionYear = jo.get("prdtYear").getAsString();
                    if (productionYear != null && !productionYear.isEmpty()) {
                        movie.setProductionYear(Integer.parseInt(productionYear));
                    }

                    String openDate = jo.get("openDt").getAsString();
                    if (openDate != null && !openDate.isEmpty()) {
                        try {
                            movie.setOpenDate(dateFormat.parse(openDate));
                        } catch (ParseException e) {
                            // 예외 처리
                            e.printStackTrace();
                        }
                    }

                    String nation = jo.get("nationAlt").getAsString();
                    if (nation != null && !nation.isEmpty()) {
                        movie.setNation(nation);
                    }

                    List<String> directorNames = new ArrayList<>();
                    for (JsonElement je2 : jo.getAsJsonArray("directors")) {
                        JsonObject jo2 = je2.getAsJsonObject();
                        directorNames.add(jo2.get("peopleNm").getAsString());
                    }

                    if (directorNames.size() > 0) {
                        movie.setDirectorList(String.join(",", directorNames));
                    }

                    movieRepository.save()

                    createdMovieIds.add(1);
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
