package com.lab49.bd.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab49.bd.integration.FlowConfiguration;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Project;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

  @Mock
  JiraConfigProperties jiraConfigProperties;

  CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
  CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);

  @Mock
  StatusLine statusLine;

  @Mock
  HttpEntity httpEntity;

  String createIssueResponse = "{\n"
      + "  \"id\": \"10502\",\n"
      + "  \"key\": \"TES-27\",\n"
      + "  \"self\": \"http://localhost:8080/rest/api/latest/issue/10502\"\n"
      + "}";

  String getIssuesResponse = "{\n"
      + "  \"expand\": \"names,schema\",\n"
      + "  \"startAt\": 0,\n"
      + "  \"maxResults\": 50,\n"
      + "  \"total\": 1,\n"
      + "  \"issues\": [\n"
      + "    {\n"
      + "      \"expand\": \"operations,versionedRepresentations,editmeta,changelog,renderedFields\",\n"
      + "      \"id\": \"10705\",\n"
      + "      \"self\": \"http://localhost:8080/rest/api/latest/issue/10705\",\n"
      + "      \"key\": \"TES-17\",\n"
      + "      \"fields\": {\n"
      + "        \"summary\": \"Creating issue at 2020/02/04 13:22\",\n"
      + "        \"project\": {\n"
      + "          \"self\": \"http://localhost:8080/rest/api/2/project/10000\",\n"
      + "          \"id\": \"10000\",\n"
      + "          \"key\": \"TES\",\n"
      + "          \"name\": \"TestProject\",\n"
      + "          \"projectTypeKey\": \"business\",\n"
      + "          \"avatarUrls\": {\n"
      + "            \"48x48\": \"http://localhost:8080/secure/projectavatar?avatarId=10324\",\n"
      + "            \"24x24\": \"http://localhost:8080/secure/projectavatar?size=small&avatarId=10324\",\n"
      + "            \"16x16\": \"http://localhost:8080/secure/projectavatar?size=xsmall&avatarId=10324\",\n"
      + "            \"32x32\": \"http://localhost:8080/secure/projectavatar?size=medium&avatarId=10324\"\n"
      + "          }\n"
      + "        }\n"
      + "      }\n"
      + "    }\n"
      + "  ]\n"
      + "}";

  InputStream createIssueIpStream = new ByteArrayInputStream(createIssueResponse.getBytes());
  InputStream getIssuesIpStream = new ByteArrayInputStream(getIssuesResponse.getBytes());

  @InjectMocks
  private IssueService issueService = new IssueService(closeableHttpClient);

  @Test
  public void create() throws Exception {
    IssueType issueType = new IssueType();
    issueType.setId("10000");
    Project project = new Project();
    project.setKey("TES");
    Fields fields = new Fields();
    fields.setProject(project);
    fields.setIssuetype(issueType);
    fields.setSummary("Creating a test issue");
    JiraIssue jiraIssue = new JiraIssue();
    jiraIssue.setFields(fields);

    when(jiraConfigProperties.getUsername()).thenReturn("admin");
    when(jiraConfigProperties.getPassword()).thenReturn("admin");
    when(closeableHttpClient.execute(any())).thenReturn(httpResponse);
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
    when(httpResponse.getEntity()).thenReturn(httpEntity);
    when(httpEntity.getContent()).thenReturn(createIssueIpStream);

    issueService.create(jiraIssue);
  }

  @Test
  public void get() throws Exception {
    when(jiraConfigProperties.getUsername()).thenReturn("admin");
    when(jiraConfigProperties.getPassword()).thenReturn("admin");
    when(closeableHttpClient.execute(any())).thenReturn(httpResponse);
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(httpResponse.getEntity()).thenReturn(httpEntity);
    when(httpEntity.getContent()).thenReturn(getIssuesIpStream);

    issueService.get("TES", FlowConfiguration.getFormattedDateTime());
  }

  @Test
  public void updateStatus() throws Exception {
  }

  @Test
  public void addComment() throws Exception {
  }

}