package com.lab49.bd.integration;

import com.lab49.bd.http.Issue;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Project;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowConfiguration {
  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Bean
  public Step createIssueInJira() {
    return stepBuilderFactory.get("createIssueStep")
        .tasklet((stepContribution, chunkContext) -> {
          Fields fields = new Fields(new Project("RAPI"), new IssueType("10000"), "Created from sprint batch step :)");
          JiraIssue jiraIssue = new JiraIssue(fields);
          Issue issue = new Issue();
          issue.create("http://localhost:8080/rest/api/latest/issue", jiraIssue);
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  @Bean
  public Flow jiraIssueCreationFlow() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("jiraIssueCreationFlow");
    flowBuilder.start(createIssueInJira())
        .end();
    return flowBuilder.build();

  }
}
