package com.lab49.bd.config;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="jira")
@Data
@NoArgsConstructor
public class JiraConfigProperties {
  @NotNull
  private String username;
  @NotNull
  private String password;
}