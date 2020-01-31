package com.lab49.bd.integration;

import com.lab49.bd.http.Issue;
import com.lab49.bd.http.Issue.Status;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Project;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  @Autowired
  public Issue issue;

  private String getFormattedDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    return dateTime.format(formatter);
  }

  @Bean
  public Step createIssueInJira() {
    return stepBuilderFactory.get("createIssueStep")
        .tasklet((stepContribution, chunkContext) -> {
          IssueType issueType = new IssueType();
          issueType.setId("10000");
          Project project = new Project();
          project.setKey("RAPI");
          Fields fields = new Fields();
          fields.setProject(project);
          fields.setIssuetype(issueType);
          fields.setSummary("Creating issue at " + getFormattedDateTime());
          JiraIssue jiraIssue = new JiraIssue();
          jiraIssue.setFields(fields);
          issue.create("http://localhost:8080/rest/api/latest/issue", jiraIssue);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step getAllIssuesAfterLastSync() {
    return stepBuilderFactory.get("getIssuesStep")
        .tasklet((stepContribution, chunkContext) -> {
          issue.get("http://localhost:8080/rest/api/latest/search", "RAPI", "2020/01/31 10:40");
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step updateStatusOfIssue() {
    return stepBuilderFactory.get("updateIssuesStatus")
        .tasklet((stepContribution, chunkContext) -> {
          issue.updateStatus("RAPI-43", Status.IN_PROGRESS);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Flow jiraIssueCreationFlow() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("jiraIssueCreationFlow");
    flowBuilder.start(getAllIssuesAfterLastSync())
        .next(createIssueInJira())
        .next(updateStatusOfIssue())
        .end();
    return flowBuilder.build();

  }
}
