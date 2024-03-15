package org.stepup.cinesquareapis.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stepup.cinesquareapis.user.entity.UserReport;
import org.stepup.cinesquareapis.user.service.UserReportService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/user/report")
@RestController
public class UserReportController {

    private final UserReportService userReportService;

    /**
     * user별 영화 별점 조회
     *
     * 아래와같이 두가지의 경우가 존재
     * 1. userId 만으로 조회해서 전체 정보를 가지고 오는 경우
     * 2. userId, movieId 두가지로 하나에 해당하는 정보만 가지고 오는경우
     *
     * @param userId, movieId
     * @reteurn userReport
     */
    @GetMapping("search")
    public ResponseEntity<List<UserReport>> searchMovieReport(@RequestParam("userId") String userId, @RequestParam(value = "movieId", required = false) String movieId) {
        List<UserReport> result = userReportService.searchMovieReport(userId, movieId);
        return new ResponseEntity<List<UserReport>>(result, HttpStatus.OK);
    }


    /**
     * user별 영화 별점 부과
     *
     * @param userReport
     * @reteurn
     */
    @PostMapping("insert")
    public ResponseEntity<String> reportMovie(@RequestBody UserReport userReport) {
        userReportService.reportMovie(userReport);
        return new ResponseEntity<>("성공", HttpStatus.OK);
    }

}
