package com.lab49.bd.integration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchTestConfig {
  @Bean
  JobLauncherTestUtils jobLauncherTestUtils() {
    return new JobLauncherTestUtils();
  }

  @Bean
  JobRepositoryTestUtils jobRepositoryTestUtils() {
    return new JobRepositoryTestUtils();
  }
}
