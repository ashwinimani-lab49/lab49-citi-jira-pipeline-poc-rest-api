package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class JiraIssues {
  private String expand;
  private Integer startAt;
  private Integer maxResults;
  private Integer total;
  private List<JiraIssue> issues;
}
