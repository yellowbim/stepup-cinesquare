package org.stepup.cinesquareapis.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbSchedulerService {
    private final MovieDbLoadingService movieDbLoadingService;

    @Bean
    public ScheduledTaskService scheduledTaskService() {
        return new ScheduledTaskService(movieDbLoadingService);
    }
}