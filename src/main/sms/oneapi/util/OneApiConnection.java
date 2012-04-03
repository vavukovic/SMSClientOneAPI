package sms.oneapi.util;

import java.net.HttpURLConnection;

import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.model.Authorization.AuthType;

public class OneApiConnection {
	public static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";	
	public static final String CHAR_ENCODING = "UTF-8";
	public static final int OK=200;
	public static final int CREATED=201;
	
	public static HttpURLConnection setupConnection(String url, OneAPIConfig oneAPIConfig) throws Exception {
		return setupConnection(url, null, oneAPIConfig);
	}

	public static HttpURLConnection setupConnection(String url, String contentType, OneAPIConfig oneAPIConfig) throws Exception {
		HttpURLConnection connection = null;
		
		if (oneAPIConfig.getAuthorization().getType().equals(AuthType.BASIC)) {	
			String username = oneAPIConfig.getAuthorization().getUsername();
			String password = oneAPIConfig.getAuthorization().getPassword();
			connection = JSONRequest.setupConnectionWithCustomAuthorization(url, "Basic", JSONRequest.getAuthorisationHeader(username, password));
		
		} else if (oneAPIConfig.getAuthorization().getType().equals(AuthType.OAUTH)) {
			String oAuthAccessToken = oneAPIConfig.getAuthorization().getAccessToken();
			connection = JSONRequest.setupConnectionWithCustomAuthorization(url, "OAuth", oAuthAccessToken);
		}

		if (contentType != null) connection.setRequestProperty("Content-Type", contentType);

		return connection;
	}
}
