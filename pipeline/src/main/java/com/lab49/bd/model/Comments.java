package com.lab49.bd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Comments {
  private Integer startAt;
  private Integer maxResults;
  private Integer total;
  private List<Comment> comments;

}
