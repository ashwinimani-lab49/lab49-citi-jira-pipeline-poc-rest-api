package com.lab49.bd.integration;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Bean
  public Job jiraIntegrationJob(Flow jiraIssueCreationFlow) {
  SimpleDateFormat simpleDateFomat = new SimpleDateFormat("yyyyMMddHHmmss");
  String dateString = simpleDateFomat.format(new Date());
  return jobBuilderFactory.get(String.format("jiraInterationJob%s", dateString))
      .start(jiraIssueCreationFlow)
      .end()
      .build();
}

}
