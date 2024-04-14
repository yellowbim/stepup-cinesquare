package org.stepup.cinesquareapis;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //스케줄링 활성화
@EnableBatchProcessing
@SpringBootApplication
public class CinesquareApisApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinesquareApisApplication.class, args);
	}

}