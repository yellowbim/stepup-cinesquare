package org.stepup.cinesquareapis.movie.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.stepup.cinesquareapis.movie.entity.LogMovieLoading;
import org.stepup.cinesquareapis.movie.entity.Movie;
import org.stepup.cinesquareapis.movie.entity.MovieBoxoffice;
import org.stepup.cinesquareapis.movie.entity.MovieSimple;
import org.stepup.cinesquareapis.movie.repository.LogMovieLoadingRepository;
import org.stepup.cinesquareapis.movie.repository.MovieBoxofficeRepository;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;
import org.stepup.cinesquareapis.movie.repository.MovieSimpleRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

import static org.stepup.cinesquareapis.movie.contant.MovieDbLoadingConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieDbLoadingService {

    private final AmazonS3 s3client;

    private final MovieRepository movieRepository;
    private final MovieSimpleRepository movieSimpleRepository;
    private final MovieBoxofficeRepository movieBoxofficeRepository;
    private final LogMovieLoadingRepository logMovieLoadingRepository;

    /**
     * 한국영화진흥원 API 영화 생성 (최초 DB)
     * 기본정보 저장, 썸네일 저장
     *
     * @return createdMovieIds
     */

    public int[] saveKoficMovies(int currentPage, int itemPerPage, int startProductionYear, int endProductionYear, int openStartDate) {
        // 영화 API 키 및 요청 URL 설정
        String movieListUrl = KOFIC_MOVIE_LIST_API_URL + "?key=" + KOFIC_KEY;

        // 코드 수정
        // ArrayList: 병렬 환경에서 안전하지 않을 수 있으며, 동시에 여러 스레드에서 접근하면 데이터 무결성 문제가 발생할 수 있음
        // Collections.synchronizedList: createdMovieIds 리스트의 스레드 안전성을 보장
        List<Integer> createdMovieIds = Collections.synchronizedList(new ArrayList<>());

        try {
            // 1. 영화 목록 조회 API URL 생성
            String apiUrl = buildMovieListApiUrl(movieListUrl, currentPage, itemPerPage, startProductionYear, endProductionYear, openStartDate);

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

                        String koficMovieCode = movieJsonObject.get("movieCd").getAsString();

                        // 대표 장르 유효성 체크
                        if (!isValidPrimaryGenre(movieJsonObject)) {
                            reportLog(FAIL_EXCLUSION_BY_VALIDATION, koficMovieCode, null, null);

                            return;
                        }

                        // 영화 정보 저장
                        saveMovie(koficMovieCode, createdMovieIds);
                    } catch (IOException e) {
                        String message = "currentPage:" + currentPage + "|itemPerPage:" + itemPerPage + "|startProductionYear:" + startProductionYear;
                        reportLog(FAIL_KOFIC_API_ERROR, null, null, message);
                        e.printStackTrace();
                    }
                }));
            }

            // 모든 작업이 완료될 때까지 대기
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    reportLog(FAIL_UNKNOWN_ERROR, null, null, null);
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

        return createdMovieIds.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * 한국영화진흥원 API 영화 생성 (단 건)
     * 기본정보 저장, 썸네일 저장
     *
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

        return -1;
    }

    /**
     * 한국영화진흥원 API 전 주 박스오피스 생성
     *
     *
     * @return createdMovieIds
     */
    @Transactional
    public ArrayList<Integer> saveMovieBoxoffice(int rankingCount, LocalDate date) throws IOException {
        ArrayList<Integer> createdMovieIds = new ArrayList<Integer>();

        // 1. 영화 박스오피스 목록 조회 API 호출
        String requestDate = date.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String apiUrl = KOFIC_MOVIE_BOXOFFICE_API_URL + "?key=" + KOFIC_KEY + "&weekGb=" + 0 + "&itemPerPage=" +rankingCount + "&targetDt=" + requestDate;
        JsonObject resultJsonObject = fetchJsonFromUrl(apiUrl);

        // 2. json 파싱
        try {
            JsonObject boxofficeJsonObject = resultJsonObject.getAsJsonObject("boxOfficeResult");

            String showRange = boxofficeJsonObject.get("showRange").getAsString();
            String startDate = showRange.split("~")[0];
            String endDate = showRange.split("~")[1];
            int yearWeekTime = boxofficeJsonObject.get("yearWeekTime").getAsInt();

            // 3. 각 영화 별로 DB 조회 및 필요시 적재
            JsonArray movieJsonArray = boxofficeJsonObject.getAsJsonArray("weeklyBoxOfficeList");
            int i=0;
            for (JsonElement movieElement : movieJsonArray) {
                i++;
                try {
                    JsonObject movieJsonObject = movieElement.getAsJsonObject();
                    String koficMovieCode = movieJsonObject.get("movieCd").getAsString();
                    int showCnt = movieJsonObject.get("showCnt").getAsInt();

                    // 3-1. 저장된 영화인지 조회
                    Movie movie = movieRepository.findByKoficMovieCode(koficMovieCode);
                    if (movie == null) {
                        // 3-2. 영화 DB 적재
                        saveMovie(koficMovieCode, createdMovieIds);
                        movie = movieRepository.findByKoficMovieCode(koficMovieCode);
                    }

                    // 4. 박스오피스 저장
                    MovieBoxoffice movieBoxoffice = new MovieBoxoffice();
                    movieBoxoffice.setMovieId(movie.getMovieId());
                    movieBoxoffice.setRank(i);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    movieBoxoffice.setStartDate(LocalDate.parse(startDate, formatter));
                    movieBoxoffice.setEndDate(LocalDate.parse(endDate, formatter));
                    movieBoxoffice.setShowCount(showCnt);
                    movieBoxoffice.setYearWeek(yearWeekTime);

                    movieBoxofficeRepository.save(movieBoxoffice);
                } catch (IOException e) {
                    String message = "rank:"+ i + "|date:" + date;
                    reportLog(FAIL_LOADING_BOXOFFICE, null, null,  message);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            String message = "fail parsing|date:" + date;
            reportLog(FAIL_LOADING_BOXOFFICE, null, null,  message);
            e.printStackTrace();
        }

        return createdMovieIds;
    }

    // 영화 목록 호출 API URL 생성
    private String buildMovieListApiUrl(String baseUrl, int currentPage, int itemPerPage, int productionYear, int endProductionYear, int openStartDate) {
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
        if (endProductionYear > 0) {
            apiUrlBuilder.append("&prdtEndYear=").append(endProductionYear);
        }
        if (openStartDate > 0) {
            apiUrlBuilder.append("&openStartDt=").append(openStartDate);
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
        if (!hasValidRunningTime(movieInfo)) {
            reportLog(FAIL_EXCLUSION_BY_VALIDATION, koficMovieCode, null, null);
            return;
        }

        // 유효성 체크2
        if (!hasValidGenreAndRating(movieInfo)) {
            reportLog(FAIL_EXCLUSION_BY_VALIDATION, koficMovieCode, null, null);
            return;
        }

        // 저장할 데이터 movie, movieDetail 객체로 생성
        MovieSimple movieSimple = new MovieSimple();
        Movie movie = new Movie();

        // 필수 값: genre, movie_title, running_time, production_year, source, kofic_movie_code
        movieSimple.setTitle(movieInfo.get("movieNm").getAsString());
        movieSimple.setRunningTime(movieInfo.get("showTm").getAsShort());
        movieSimple.setProductionYear(movieInfo.get("prdtYear").getAsShort());

        movie.setTitle(movieInfo.get("movieNm").getAsString());
        movie.setRunningTime(movieInfo.get("showTm").getAsShort());
        movie.setProductionYear(movieInfo.get("prdtYear").getAsShort());
        movie.setSource((short)1);
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
            movie.setTitleEn(movieTitleEn);
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

        if (!actors.isEmpty()) {
            List<String> relevantActors = (actors.size() > 15) ? actors.subList(0, 15) : actors;
            movie.setActors(String.join(",", relevantActors));
        }

        // tb_movie 저장
        Movie createdMovie = movieRepository.save(movie);

        // tb_movie_simple 저장
        movieSimple.setMovieId(createdMovie.getMovieId());
        movieSimpleRepository.save(movieSimple);

        reportLog(SUCCESS, koficMovieCode, movieSimple.getMovieId(), null);

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

    // 영화 저장 로그 남기기
    private void reportLog(short status, String koficMovieCode, Integer movieId, String message) {
        LogMovieLoading log = new LogMovieLoading();

        log.setStatus(status);
        if (koficMovieCode != null) {
            log.setKoficMovieCode(koficMovieCode);
        }
        if (movieId != null) {
            log.setMovieId(movieId);
        }
        if (message != null) {
            log.setMessage(message);
        }

        logMovieLoadingRepository.save(log);
    }


    // CINE movie_id로 크롤링 데이터 업데이트
    @Transactional
    public void crawlMovieSubInfo(int[] movieIds) {
        for (int movieId : movieIds) {
            Movie movie = movieRepository.findById(movieId).orElse(null);

            String koficMovieCode = movie.getKoficMovieCode();

            String synopsys = null;
            String imageIds = null;

            if (movie != null) {
                // HTML 파싱
                Document doc = getMovieSubInfoHtml(koficMovieCode);

                Elements elements = doc.select("div > div.item_tab.basic");
                // 1. 썸네일 이미지 크롤링
                try {
                    Elements x = elements.select("div.ovf.info.info1 > a");
                    if (x.size() > 0) {
                        String koficImageUrl = x.get(0).attr("href");

                        if (!koficImageUrl.equals("#")) {
                            // S3에 이미지 저장
                            String savePath = "movies/" + movieId;
                            String filename = "thumbnail";
                            downloadImage(koficImageUrl, savePath, filename);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 로그 DB 저장
                    LogMovieLoading log = new LogMovieLoading();
                    log.setStatus(FAIL_CRAWLLING_THUMBNAIL);
                    log.setMovieId(movieId);
                    log.setKoficMovieCode(koficMovieCode);
                }

                // 2. 스틸컷 이미지 1개 크롤링
                try {
                    Elements x = elements.select("div:nth-child(4) > div.thumb_slide > div");
                    if (x.size() > 0) {
                        String koficImageUrl = x.get(0).select("img").attr("src");

                        // S3에 이미지 저장
                        String savePath = "movies/" + movieId + "/images";
                        imageIds = "1";
                        downloadImage(koficImageUrl, savePath, imageIds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 3. 시놉시스 html 크롤링
                try {
                    Elements x = elements.select("div:nth-child(5) > p");
                    if (x.size() > 0) {
                        synopsys = x.get(0).toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 영화 DB 업데이트
                movieRepository.updateThumbnailToTrue(movieId);
                movieSimpleRepository.updateThumbnailToTrue(movieId);
                if (synopsys != null) {
                    movieRepository.updateSynopsys(synopsys, movieId);
                }
                if (imageIds != null) {
                    movieRepository.updateImages(imageIds, movieId);
                }

                // 로그 DB 저장
                LogMovieLoading log = new LogMovieLoading();
                log.setStatus(UPDATE_THUMBNAIL);
                log.setMovieId(movieId);
                log.setKoficMovieCode(koficMovieCode);
            } else {
                // 로그 DB 저장
                LogMovieLoading log = new LogMovieLoading();
                log.setStatus(FAIL_FIND_MOVIE_IN_DB);
                log.setMovieId(movieId);
                log.setKoficMovieCode(koficMovieCode);
            }
        }
    }

    // KOFIC CODE
    @Transactional
    public void crawlAndDownloadImages(String[] koficMovieCodes) {
        for (String koficMovieCode : koficMovieCodes) {

            // HTML 파싱
            Document doc = getMovieSubInfoHtml(koficMovieCode);

            // Selector로 접근 후 href 요소 찾기
            Elements elements = doc.select("div > div.item_tab.basic > div.ovf.info.info1 > a");
            String koficImageUrl = elements.get(0).attr("href");

            String savePath = "movies/" + koficMovieCode;
            String filename = "thumbnail";

            // S3에 이미지 저장
            downloadImage(koficImageUrl, savePath, filename);
        }
    }

    // 영화 코드를 아용해 이미지를 다운 받기 위해 HTML 파싱
    private Document getMovieSubInfoHtml(String koficMovieCode) {
        // HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

        // form-data로 전송할 데이터
        String requestBody = "titleYN=Y&&isOuterReq=false&CSRFToken=jynKyMeKA46R5TKEL27l0DVPpBXehuXMuV1i5K9XWc4&code=" + koficMovieCode;

        // HttpEntity 생성
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();

        // POST 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.exchange(KOFIC_MOVIE_INFO_CRAWLLING_URL, HttpMethod.POST, requestEntity, String.class);

        // 응답의 HTML 파싱하기
        String html = responseEntity.getBody();
        Document doc = Jsoup.parse(html);

        return doc;
    }

    // S3에 이미지 저장
    private void downloadImage(String imageUrl, String savePath, String filename) {
        try {
            String urlPrefix = "https://kobis.or.kr";
            URL url = new URL(urlPrefix + imageUrl);
            InputStream inputStream = url.openStream();

            String extension = "";
            int i = imageUrl.lastIndexOf('.');
            if (i > 0) {
                extension = imageUrl.substring(i);
            }

            String key = savePath + "/" + filename + extension;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg"); // 적절한 MIME 타입 설정

            // S3에 이미지 업로드
            s3client.putObject(new PutObjectRequest(S3_BUCKET_NAME, key, inputStream, metadata));

            System.out.println("이미지 업로드 완료: " + key);

            inputStream.close(); // 스트림 닫기
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 이미지 로컬 다운로드
    /*
    private void downloadImage(String imageUrl, String savePath, int movieCode) {
        try {
            // URL 연결
            String urlPrefix = "https://kobis.or.kr";
            URL url = new URL(urlPrefix+imageUrl);
            InputStream inputStream = url.openStream();

            // 이미지 URL에서 확장자 추출
            String extension = "";
            int i = imageUrl.lastIndexOf('.');
            if (i > 0) {
                extension = imageUrl.substring(i);
            }

            // 이미지 다운로드 경로 설정 (확장자 포함)
            Path downloadPath = Paths.get(savePath, Integer.toString(movieCode) + extension);

            // 이미지 저장을 위한 FileOutputStream
            FileOutputStream outputStream = new FileOutputStream(downloadPath.toString());

            // 입력 스트림에서 바이트를 읽고 출력 스트림에 쓰기
            byte[] buffer = new byte[2048];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            // 스트림 닫기
            inputStream.close();
            outputStream.close();

            System.out.println("이미지 다운로드 완료: " + downloadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
