package com.lab49.bd.model;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class Fields {
  @NotNull
  private final Project project;
  @NotNull
  private final IssueType issuetype;
  @NotNull
  private final String summary;
}
