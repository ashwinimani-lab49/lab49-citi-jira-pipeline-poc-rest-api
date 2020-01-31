package com.lab49.bd.http;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lab49.bd.config.JiraConfigProperties;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.JiraIssues;
import com.lab49.bd.url.JiraRESTAPI;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
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
  final static ResponseHandler<String> handler = new BasicResponseHandler();
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

  public void create(JiraIssue request) {
    try {
      HttpPost httpPost = new HttpPost(JiraRESTAPI.getIssueEndPoint());
      String jacksonJson = objectMapper.writeValueAsString(request);
      StringEntity entity = new StringEntity(jacksonJson);
      logger.warn("Create request" + jacksonJson);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_CREATED) {
        String responseBody = handler.handleResponse(response);
        JiraIssue createdIssue = objectMapper.readValue(responseBody, JiraIssue.class);
        logger.warn("Create Issue Key:  " + createdIssue.getKey());
      } else {
        logger.warn("Create Response:  " + statusCode);
      }
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
    } catch (URISyntaxException e) {
      logger.error("URISyntaxException Error", e);
    }
  }

  public void get(String projectKey, String updatedAfter) {
    String requestBody = "{\"jql\": \"project = " + projectKey + " AND updated > '" + updatedAfter + "'\",\"fields\": [\"*all\"]}";
    try {
      HttpPost httpPost = new HttpPost(JiraRESTAPI.getSearchEndPoint());
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      String responseBody = handler.handleResponse(response);
      logger.warn("Get Response: " + responseBody);
      JiraIssues jiraIssues = objectMapper.readValue(responseBody, JiraIssues.class);
      Optional<JiraIssue> jiraIssue = jiraIssues.getIssues().stream().findFirst();
      jiraIssue.ifPresent(issue -> logger.warn("Get JiraIssue: " + issue.getKey()));
    } catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocol Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    } catch (URISyntaxException e) {
      logger.error("URISyntaxException Error", e);
    }

  }

  public void updateStatus(String issueKey, Status status) {
    String requestBody = "{\"transition\": {\"id\": \"" + status.transitionID + "\"}}";
    logger.warn("Update request: " + requestBody);
    try {
      String url = JiraRESTAPI.getIssueEndPoint() + "/" + issueKey + "/" +"/transitions";
      HttpPost httpPost = new HttpPost(url);
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      logger.warn("Update status Response: " + response.getStatusLine());
    }catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    } catch (URISyntaxException e) {
      logger.error("URISyntaxException Error", e);
    }
  }

  private HttpPost setHeader(HttpPost httpPost, StringEntity entity) throws AuthenticationException{
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.addHeader(new BasicScheme().authenticate(getUserCredentials(), httpPost, null));
    return httpPost;
  }

  private UsernamePasswordCredentials getUserCredentials() {
    return new UsernamePasswordCredentials(jiraConfigProperties.getUsername(), jiraConfigProperties.getPassword());
  }
}
