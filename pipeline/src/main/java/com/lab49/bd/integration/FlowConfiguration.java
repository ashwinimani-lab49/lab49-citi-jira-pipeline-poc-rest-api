package com.lab49.bd.integration;

import com.lab49.bd.http.Issue;
import com.lab49.bd.http.Issue.Status;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
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
          project.setKey("TES");
          Fields fields = new Fields();
          fields.setProject(project);
          fields.setIssuetype(issueType);
          fields.setSummary("Creating issue at " + getFormattedDateTime());
          JiraIssue jiraIssue = new JiraIssue();
          jiraIssue.setFields(fields);
          issue.create(jiraIssue);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step getAllIssuesAfterLastSync() {
    return stepBuilderFactory.get("getIssuesStep")
        .tasklet((stepContribution, chunkContext) -> {
          issue.get("TES", getFormattedDateTime());
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step updateStatusOfIssue() {
    return stepBuilderFactory.get("updateIssuesStatus")
        .tasklet((stepContribution, chunkContext) -> {
          issue.updateStatus("TES-16", Status.IN_PROGRESS);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step addComment() {
    return stepBuilderFactory.get("addComment")
        .tasklet((stepContribution, chunkContext) -> {
          issue.addComment("TES-16", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step testJsonSchema() {
    return stepBuilderFactory.get("testJsonSchema")
        .tasklet((stepContribution, chunkContext) -> {
          JSONObject jsonSchema = new JSONObject(
            new JSONTokener(FlowConfiguration.class.getResourceAsStream("/fieldMappingsSchema.json"))
          );
          JSONArray jsonSubject = new JSONArray(
              new JSONTokener(FlowConfiguration.class.getResourceAsStream("/fieldMappings.json"))
          );
          Schema schema = SchemaLoader.load(jsonSchema);
          schema.validate(jsonSubject);
          System.out.println(jsonSubject);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Flow jiraIssueCreationFlow() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("jiraIssueCreationFlow");
//    flowBuilder.start(createIssueInJira())
//        .next(getAllIssuesAfterLastSync())
//        .next(updateStatusOfIssue())
//        .next(addComment())
//        .end();
    flowBuilder.start(testJsonSchema()).end();
    return flowBuilder.build();

  }
}
