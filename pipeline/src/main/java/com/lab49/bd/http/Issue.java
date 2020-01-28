package com.lab49.bd.http;

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

public class Issue {
  final static Logger logger = Logger.getLogger(Issue.class);

  public static void create(String Url) {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(Url);
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("amani", "admin");
    String json = "{\n"
        + "\t\"fields\": {\n"
        + "\t\t\"project\": {\n"
        + "\t\t\t\"key\": \"RAPI\"\n"
        + "\t\t},\n"
        + "\t\t\"issuetype\": {\n"
        + "\t\t\t\"id\": \"10000\"\n"
        + "\t\t},\n"
        + "\t\t\"summary\": \"something's wrong from Apache HttpClient!\"\n"
        + "\t}\n"
        + "}";
    try {
      StringEntity entity = new StringEntity(json);
      httpPost.setEntity(entity);
      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-type", "application/json");
      httpPost.addHeader(new BasicScheme().authenticate(credentials, httpPost, null));
      CloseableHttpResponse response = client.execute(httpPost);
      logger.trace("Response: " + response);
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