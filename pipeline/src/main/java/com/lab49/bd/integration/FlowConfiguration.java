package com.lab49.bd.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
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

  private final Issue issue;

  public FlowConfiguration(Issue issue) {
    this.issue = issue;
  }

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
          issue.updateStatus("TES-17", Status.IN_PROGRESS);
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step addComment() {
    return stepBuilderFactory.get("addComment")
        .tasklet((stepContribution, chunkContext) -> {
          issue.addComment("TES-17", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step testJsonSchema() {
    return stepBuilderFactory.get("testJsonSchema")
        .tasklet((stepContribution, chunkContext) -> {
          JsonNode jsonSchema = JsonLoader.fromResource("/fieldMappingsSchema.json");
          JsonNode jsonSubject = JsonLoader.fromResource("/fieldMappings.json");
          JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema);
          if (schema.validate(jsonSubject).isSuccess()) {
            System.out.println(jsonSubject);
          }
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Flow jiraIssueCreationFlow() {
    FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("jiraIssueCreationFlow");
    flowBuilder.start(createIssueInJira())
        .next(getAllIssuesAfterLastSync())
        .next(updateStatusOfIssue())
        .next(addComment())
        .next(testJsonSchema())
        .end();

//    flowBuilder.start(addComment())
//        .next(getAllIssuesAfterLastSync())
//        .next(testJsonSchema())
//        .end();
    return flowBuilder.build();

  }
}
