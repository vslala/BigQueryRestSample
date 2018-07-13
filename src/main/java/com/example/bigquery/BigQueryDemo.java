package com.example.bigquery;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.CharStreams;

public class BigQueryDemo {
	
	private static final String QUERY_URL_FORMAT = "https://www.googleapis.com/bigquery/v2/projects/%s/queries" + "?access_token=%s";

	private static final String QUERY = "query";

	private static final String QUERY_HACKER_NEWS_COMMENTS = "SELECT * FROM [bigquery-public-data:hacker_news.comments] LIMIT 1000";

	private static final Logger logger = Logger.getLogger(BigQueryDemo.class);
	
	static GoogleCredential credential = null;
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	static {
		// Authenticate requests using Google Application Default credentials.
		try {
			credential = GoogleCredential.getApplicationDefault();
			credential = credential.createScoped(Arrays.asList("https://www.googleapis.com/auth/bigquery"));
			credential.refreshToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void implicit() {
		String projectId = credential.getServiceAccountProjectId();
		String accessToken = generateAccessToken();
		// Set the content of the request.
		Dataset dataset = new Dataset().addLabel(QUERY,	QUERY_HACKER_NEWS_COMMENTS);
		HttpContent content = new JsonHttpContent(JSON_FACTORY, dataset.getLabels());
		// Send the request to the BigQuery API.
		GenericUrl url = new GenericUrl(String.format(QUERY_URL_FORMAT, projectId, accessToken));
		logger.debug("URL: " + url.toString());
		String responseJson = getQueryResult(content, url);
		logger.debug(responseJson);
	}

	private static String getQueryResult(HttpContent content, GenericUrl url) {
		String responseContent = null;
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
		HttpRequest request = null;
		try {
			request = requestFactory.buildPostRequest(url, content);
			request.setParser(JSON_FACTORY.createJsonObjectParser());
			request.setHeaders(
					new HttpHeaders().set("X-HTTP-Method-Override", "POST").setContentType("application/json"));
			HttpResponse response = request.execute();
			InputStream is = response.getContent();
			responseContent = CharStreams.toString(new InputStreamReader(is));
		} catch (IOException e) {
			logger.error(e);
		}
		return responseContent;
	}

	private static String generateAccessToken() {
		String accessToken = null;
		if ((System.currentTimeMillis() > credential.getExpirationTimeMilliseconds())) {
			accessToken = credential.getRefreshToken();
		} else {
			accessToken = credential.getAccessToken();
		}
		System.out.println(accessToken);
		return accessToken;
	}
}
