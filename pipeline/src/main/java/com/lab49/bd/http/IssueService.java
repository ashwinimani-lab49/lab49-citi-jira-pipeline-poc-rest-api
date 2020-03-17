package com.lab49.bd.http;

import com.lab49.bd.model.Comment;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.JiraIssues;
import com.lab49.bd.url.JiraRESTAPI;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IssueService {
  final static Logger logger = Logger.getLogger(IssueService.class);
  @Autowired
  private JiraConfigProperties jiraConfigProperties;
  private final CloseableHttpClient client;
  final static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  static {
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.setSerializationInclusion(Include.NON_EMPTY);
  }

  public IssueService(CloseableHttpClient client) {
    this.client = client;
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
      logger.warn("Create request: " + jacksonJson);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_CREATED) {
        JiraIssue createdIssue = objectMapper.readValue(response.getEntity().getContent() , JiraIssue.class);
        logger.warn("Created Issue Key:  " + createdIssue.getKey());
      } else {
        logger.warn("Issue not created. Create Response:  " + statusCode);
      }
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocolException Error", e);
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessingException Error", e);
    } catch (UnsupportedEncodingException e) {
      logger.error("UnsupportedEncodingException Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (IOException e) {
      logger.error("IOException Error", e);
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
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        JiraIssues jiraIssues = objectMapper.readValue(response.getEntity().getContent(), JiraIssues.class);
        logger.warn("Total issues updated after " + updatedAfter + ": " + jiraIssues.getTotal());
        Optional<JiraIssue> jiraIssue = jiraIssues.getIssues().stream().findFirst();
        jiraIssue.ifPresent(issue -> logger.warn("First JiraIssue: " + issue.getKey()));
      } else {
        logger.warn("Could not get issues:  " + statusCode);
      }
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocolException Error", e);
    } catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
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
      String url = JiraRESTAPI.getIssueEndPoint() + "/" + issueKey +"/transitions";
      HttpPost httpPost = new HttpPost(url);
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_NO_CONTENT) {
        logger.warn("Status of issue " + issueKey + " updated.");
      } else {
        logger.warn("Could not update status of issue " + issueKey + ". Server response: " + statusCode);
      }
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocolException Error", e);
    } catch (UnsupportedEncodingException e) {
      logger.error("Json Encoding Error", e);
    } catch (AuthenticationException e) {
      logger.error("Authentication Error", e);
    } catch (IOException e) {
      logger.error("I/O Error", e);
    } catch (URISyntaxException e) {
      logger.error("URISyntaxException Error", e);
    }
  }

  public void addComment(String issueKey, String commentBody) {
    String requestBody = "{\"body\": \"" + commentBody +"\"}";
    try {
      String url = JiraRESTAPI.getIssueEndPoint() + "/" + issueKey +"/comment";
      HttpPost httpPost = new HttpPost(url);
      StringEntity entity = new StringEntity(requestBody);
      HttpResponse response = client.execute(setHeader(httpPost, entity));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_CREATED) {
        Comment addedComment = objectMapper.readValue(response.getEntity().getContent(), Comment.class);
        logger.warn("Added comment: " + addedComment.getBody() + "at: " + addedComment.getCreated());
      } else {
        logger.warn("Could not add comment to issue " + issueKey + ". Server response: " + statusCode);
      }
    } catch (ClientProtocolException e) {
      logger.error("ClientProtocolException Error", e);
    } catch (UnsupportedEncodingException e) {
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
