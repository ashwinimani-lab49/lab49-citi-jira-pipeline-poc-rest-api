package com.lab49.bd;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableBatchProcessing
@EnableScheduling
public class JiraIntegrationRestApplication {

  public static void main(String[] args) {
    SpringApplication.run(JiraIntegrationRestApplication.class, args);
  }

}