package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Status {
  private String self;
  private String description;
  private String name;
  private String id;
  private StatusCategory statusCategory;
}
