package org.stepup.cinesquareapis.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stepup.cinesquareapis.movie.entity.MovieReport;
import org.stepup.cinesquareapis.movie.model.ReadMovieReportResponse;
import org.stepup.cinesquareapis.movie.model.SaveMovieReportRequest;
import org.stepup.cinesquareapis.movie.repository.MovieReportRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieReportService {

    private final MovieReportRepository movieRepository;

    /**
     * 영화 1, 다중 사용자 리뷰 조회
     * return ReadMovieReportResponse
     */
    public List<ReadMovieReportResponse> readMovieUsersReport(Integer movieId) {
        List<MovieReport> movieReports = movieRepository.findAllByMovieId(movieId); // 조회는 Repository에 있는 MovieReport Entity에서만 조회 가능

        return movieReports.stream().map(ReadMovieReportResponse::new).collect(Collectors.toList()); // List를 받기 위해서 stream으로 처리를 해줘야함!
    }

    /**
     * 영화 1, 사용자 리뷰 등록
     */
    public void saveMovieReport(Integer movieId, Integer userId, SaveMovieReportRequest request) {
        request.setMovieId(movieId);
        request.setUserId(userId);
        movieRepository.save(request.toEntity());

        // 만약 해당 정보의 보고싶어요 = 0, 코멘트, 점수가 하나도 없다면 해당 줄 삭제

    }
}
