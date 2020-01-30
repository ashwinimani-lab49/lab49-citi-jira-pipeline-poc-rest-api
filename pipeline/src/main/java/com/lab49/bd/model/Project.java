package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Project {
  private String self;
  private String id;
  private String key;
  private String name;
  private String projectTypeKey;
}
