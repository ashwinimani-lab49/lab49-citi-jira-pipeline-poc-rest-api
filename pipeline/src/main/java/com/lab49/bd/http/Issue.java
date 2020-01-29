package com.lab49.bd.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab49.bd.config.JiraConfigProperties;
import com.lab49.bd.model.JiraIssue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Issue {
  final static Logger logger = Logger.getLogger(Issue.class);
  @Autowired
  private JiraConfigProperties jiraConfigProperties;

  public void create(String Url, JiraIssue request) {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(Url);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("amani", "admin");
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String jacksonJson = objectMapper.writeValueAsString(request);
      StringEntity entity = new StringEntity(jacksonJson);
      httpPost.setEntity(entity);
      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-type", "application/json");
      httpPost.addHeader(new BasicScheme().authenticate(credentials, httpPost, null));
      CloseableHttpResponse response = client.execute(httpPost);
      logger.trace("Response: " + response.getStatusLine());
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessing Error when Jackson tried to convert", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocol Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    }
  }

}
