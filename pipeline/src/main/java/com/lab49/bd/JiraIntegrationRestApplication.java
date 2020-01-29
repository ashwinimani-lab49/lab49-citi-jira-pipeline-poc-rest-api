package com.lab49.bd;

import com.lab49.bd.http.Issue;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.Project;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableBatchProcessing
public class JiraIntegrationRestApplication {
  public static void main(String[] args) {
    Fields fields = new Fields(new Project("RAPI"), new IssueType("10000"), "Renamed to JiraIssue");
    JiraIssue jiraIssue = new JiraIssue(fields);
    Issue issue = new Issue();
    issue.create("http://localhost:8080/rest/api/latest/issue", jiraIssue);
    SpringApplication.run(JiraIntegrationRestApplication.class, args);
  }
}