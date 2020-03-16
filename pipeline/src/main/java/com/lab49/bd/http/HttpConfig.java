package com.lab49.bd.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HttpConfig {
  @Bean
  public HttpClient httpClient() {
    return HttpClients.createDefault();
  }

}
