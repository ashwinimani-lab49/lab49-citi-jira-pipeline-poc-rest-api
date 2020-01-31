package com.lab49.bd.http;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lab49.bd.config.JiraConfigProperties;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.JiraIssues;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Issue {
  final static Logger logger = Logger.getLogger(Issue.class);
  @Autowired
  private JiraConfigProperties jiraConfigProperties;
  final static HttpClient client = HttpClients.createDefault();
  final static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  static {
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.setSerializationInclusion(Include.NON_EMPTY);
  }

  public enum Status {
    TO_DO("31"),
    IN_PROGRESS("11"),
    DONE("41");

    public final String transitionID;

    private Status(String transitionID) {
      this.transitionID = transitionID;
    }
  }

  public void create(String Url, JiraIssue request) {
    HttpPost httpPost = new HttpPost(Url);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(jiraConfigProperties.getUsername(), jiraConfigProperties.getPassword());
    try {
      String jacksonJson = objectMapper.writeValueAsString(request);
      StringEntity entity = new StringEntity(jacksonJson);
      logger.warn("Create request" + jacksonJson);
      HttpResponse response = client.execute(setHeader(httpPost, entity, credentials));
      logger.warn("Create Response: " + response.getStatusLine());
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

  public void get(String url, String projectKey, String updatedAfter) {
    HttpPost httpPost = new HttpPost(url);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(jiraConfigProperties.getUsername(), jiraConfigProperties.getPassword());
    String requestBody = "{\"jql\": \"project = " + projectKey + " AND updated > '" + updatedAfter + "'\",\"fields\": [\"*all\"]}";
    ResponseHandler<String> handler = new BasicResponseHandler();
    try {
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity, credentials));
      String responseBody = handler.handleResponse(response);
      logger.warn("Get Response: " + responseBody);
      JiraIssues jiraIssues = objectMapper.readValue(responseBody, JiraIssues.class);
      logger.warn("Get JiraIssue: " + jiraIssues);
    } catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocol Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    }

  }

  public void updateStatus(String issueKey, Status status) {
    String url = "http://localhost:8080/rest/api/latest/issue/" + issueKey + "/transitions";
    HttpPost httpPost = new HttpPost(url);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(jiraConfigProperties.getUsername(), jiraConfigProperties.getPassword());
    String requestBody = "{\"transition\": {\"id\": \"" + status.transitionID + "\"}}";
    try {
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity, credentials));
      logger.warn("Update status Response: " + response.getStatusLine());
    }catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    }
  }

  private HttpPost setHeader(HttpPost httpPost, StringEntity entity, Credentials credentials) throws AuthenticationException{
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.addHeader(new BasicScheme().authenticate(credentials, httpPost, null));
    return httpPost;
  }

}
