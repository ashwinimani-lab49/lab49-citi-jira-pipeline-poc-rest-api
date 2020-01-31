package com.lab49.bd.url;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;

public class JiraRESTAPI {
  final static String scheme = "http";
  final static String host = "localhost";
  final static int port = 8080;
  final static String basePath = "rest/api/latest/";

  public static URI getIssueEndPoint() throws URISyntaxException {
    return new URIBuilder()
        .setScheme(scheme)
        .setHost(host)
        .setPort(port)
        .setPath(basePath + "issue")
        .build();
  }

  public static URI getSearchEndPoint() throws URISyntaxException {
    return new URIBuilder()
        .setScheme(scheme)
        .setHost(host)
        .setPort(port)
        .setPath(basePath + "search")
        .build();
  }
}
