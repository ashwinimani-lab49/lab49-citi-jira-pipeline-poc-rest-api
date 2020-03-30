package com.lab49.bd.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lab49.bd.http.IssueService.Status;
import com.lab49.bd.integration.FlowConfiguration;
import com.lab49.bd.model.Fields;
import com.lab49.bd.model.IssueType;
import com.lab49.bd.model.JiraIssue;
import com.lab49.bd.model.Project;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

  @Mock
  JiraConfigProperties jiraConfigProperties;

  private CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
  private CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);

  private String createIssueResponse = "{\n"
      + "  \"id\": \"10502\",\n"
      + "  \"key\": \"TES-27\",\n"
      + "  \"self\": \"http://localhost:8080/rest/api/latest/issue/10502\"\n"
      + "}";

  private String getIssuesResponse = "{\n"
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

  private String addCommentResponse = "{\n"
      + "  \"self\": \"http://localhost:8080/rest/api/2/issue/10616/comment/10302\",\n"
      + "  \"id\": \"10302\",\n"
      + "  \"author\": {\n"
      + "    \"self\": \"http://localhost:8080/rest/api/2/user?username=amani\",\n"
      + "    \"name\": \"amani\",\n"
      + "    \"key\": \"JIRAUSER10000\",\n"
      + "    \"emailAddress\": \"ashwini.mani@lab49.com\",\n"
      + "    \"avatarUrls\": {\n"
      + "      \"48x48\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=48\",\n"
      + "      \"24x24\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=24\",\n"
      + "      \"16x16\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=16\",\n"
      + "      \"32x32\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=32\"\n"
      + "    },\n"
      + "    \"displayName\": \"Ashwini Mani\",\n"
      + "    \"active\": true,\n"
      + "    \"timeZone\": \"GMT\"\n"
      + "  },\n"
      + "  \"body\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit.\",\n"
      + "  \"updateAuthor\": {\n"
      + "    \"self\": \"http://localhost:8080/rest/api/2/user?username=amani\",\n"
      + "    \"name\": \"amani\",\n"
      + "    \"key\": \"JIRAUSER10000\",\n"
      + "    \"emailAddress\": \"ashwini.mani@lab49.com\",\n"
      + "    \"avatarUrls\": {\n"
      + "      \"48x48\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=48\",\n"
      + "      \"24x24\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=24\",\n"
      + "      \"16x16\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=16\",\n"
      + "      \"32x32\": \"https://www.gravatar.com/avatar/16ad7a8e67d38d9ec2ee0066521ac603?d=mm&s=32\"\n"
      + "    },\n"
      + "    \"displayName\": \"Ashwini Mani\",\n"
      + "    \"active\": true,\n"
      + "    \"timeZone\": \"GMT\"\n"
      + "  },\n"
      + "  \"created\": \"2020-01-31T15:27:54.157+0000\",\n"
      + "  \"updated\": \"2020-01-31T15:27:54.157+0000\"\n"
      + "}";

  private InputStream createIssueIpStream = new ByteArrayInputStream(createIssueResponse.getBytes());
  private InputStream getIssuesIpStream = new ByteArrayInputStream(getIssuesResponse.getBytes());
  private InputStream addCommentIpStream = new ByteArrayInputStream(addCommentResponse.getBytes());

  @InjectMocks
  private IssueService issueService = new IssueService(closeableHttpClient);

  @Before
  public void setUp() throws Exception {
    when(jiraConfigProperties.getUsername()).thenReturn("admin");
    when(jiraConfigProperties.getPassword()).thenReturn("admin");
    when(closeableHttpClient.execute(any())).thenReturn(httpResponse);
  }

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

    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
    when(httpResponse.getEntity().getContent()).thenReturn(createIssueIpStream);

    issueService.create(jiraIssue);
  }

  @Test
  public void get() throws Exception {
    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(httpResponse.getEntity().getContent()).thenReturn(getIssuesIpStream);

    issueService.get("TES", FlowConfiguration.getFormattedDateTime());
  }

  @Test
  public void updateStatus() {
    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);

    issueService.updateStatus("TES-11", Status.IN_PROGRESS);
  }

  @Test
  public void addComment() throws Exception {
    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
    when(httpResponse.getEntity().getContent()).thenReturn(addCommentIpStream);

    issueService.addComment("TES-11", "Lorem ipsum dolor sit amet");
  }

}