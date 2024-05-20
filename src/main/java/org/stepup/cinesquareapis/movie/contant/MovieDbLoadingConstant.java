package org.stepup.cinesquareapis.movie.contant;

public class MovieDbLoadingConstant {
    public static final String S3_BUCKET_NAME = "cinesquares3";

    public static final String GENRE_ETC = "기타";
    public static final String GENRE_EROTIC = "성인물(에로)";
    public static final String GENRE_ROMANCE = "멜로/로맨스";
    public static final String ADULTS_ONLY = "청소년관람불가";

    public static final short SUCCESS = 0;
    public static final short UPDATE_THUMBNAIL = 1;
    public static final short FAIL_EXCLUSION_BY_VALIDATION = 2;
    public static final short FAIL_KOFIC_API_ERROR = 3;
    public static final short FAIL_FIND_MOVIE_IN_DB = 4;
    public static final short FAIL_CRAWLLING_THUMBNAIL = 5;
    public static final short FAIL_LOADING_BOXOFFICE = 6;
    public static final short FAIL_UNKNOWN_ERROR = 9;


    public static final String KOFIC_KEY = "a440544b11856d33b630de4bf58546bb";
    public static final String KOFIC_MOVIE_LIST_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
    public static final String KOFIC_MOVIE_DETAIL_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    public static final String KOFIC_MOVIE_BOXOFFICE_API_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json";
    public static final String KOFIC_MOVIE_INFO_CRAWLLING_URL = "https://kobis.or.kr/kobis/business/mast/mvie/searchMovieDtl.do";
}
