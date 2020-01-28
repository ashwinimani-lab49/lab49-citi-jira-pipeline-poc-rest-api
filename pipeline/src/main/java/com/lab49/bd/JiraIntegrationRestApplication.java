package com.lab49.bd;

import com.lab49.bd.http.Issue;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableBatchProcessing
public class JiraIntegrationRestApplication {
  public static void main(String[] args) {
    Issue.create("http://localhost:8080/rest/api/latest/issue");
    SpringApplication.run(JiraIntegrationRestApplication.class, args);
  }
}