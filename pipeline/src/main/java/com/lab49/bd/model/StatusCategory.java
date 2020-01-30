package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class StatusCategory {
  private String self;
  private Integer id;
  private String key;
  private String name;
}
