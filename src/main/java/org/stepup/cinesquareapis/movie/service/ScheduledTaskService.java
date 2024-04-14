package org.stepup.cinesquareapis.movie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;

@RequiredArgsConstructor
public class ScheduledTaskService {

    private final MovieDbLoadingService movieDbLoadingService;

    @Scheduled(cron = "0 0 9 ? * MON", zone = "Asia/Seoul") // 매주 월요일 오전 9시
    public void performTask() throws IOException {
        System.out.println("Executing task every Monday at 9 AM Seoul Time");

        LocalDate xx = LocalDate.now().minusDays(7);
        movieDbLoadingService.saveMovieBoxoffice(10,  xx);
//            movieDbLoadingService.saveMovieBoxoffice(10,  LocalDate.now());

    }
}