package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class JiraIssue {
  private String expand;
  private String id;
  private String self;
  private String key;
  private Fields fields;
}
