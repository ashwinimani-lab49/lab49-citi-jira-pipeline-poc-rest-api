package com.lab49.bd.integration;

import com.lab49.bd.config.JiraConfigProperties;
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

  @Bean
  JiraConfigProperties jiraConfigProperties() {
    JiraConfigProperties jiraConfigProperties = new JiraConfigProperties();
    jiraConfigProperties.setPassword("admin");
    jiraConfigProperties.setUsername("admin");
    return jiraConfigProperties;
  }
}
