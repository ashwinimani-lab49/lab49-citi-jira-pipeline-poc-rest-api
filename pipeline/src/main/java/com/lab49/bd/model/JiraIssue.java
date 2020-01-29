package com.lab49.bd.model;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class JiraIssue {
  @NotNull
  private final Fields fields;
}
