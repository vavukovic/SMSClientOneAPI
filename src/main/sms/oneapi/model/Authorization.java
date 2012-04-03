package sms.oneapi.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class Authorization {
	
	public enum AuthType {
		BASIC, OAUTH;
	};
	
	//Default Authorization type
	private AuthType type = AuthType.BASIC;	
	//Basic Authorization parameters
	private String username = "";
	private String password = "";
	//OAuth Authorization parameters 
	private String accessToken = "";
	private String apiKey = "";
	private String apiSecret = "";
	
	public Authorization() {  
		super();
	}
	
	/**
	 * Initialize 'BASIC' Authorization
	 * @param username
	 * @param password
	 */
	public Authorization(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Initialize 'OAUTH' Authorization
	 * @param accessToken
	 * @param apiKey
	 * @param apiSecret
	 */
	public Authorization(String accessToken, String apiKey, String apiSecret) {
		this.accessToken = accessToken;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;	
		this.type = AuthType.OAUTH;
	}

	/**
	 * Get Authorization type 
	 * @return AuthType - (AuthType.BASIC, AuthType.OAUTH)
	 */
	@JsonProperty("type")
	public AuthType getType() {
		return type;
	}
	/**
	 * Set Authorization type 
	 * @param type - (AuthType.BASIC, AuthType.OAUTH)
	 */
	@JsonProperty("type")
	public void setType(AuthType type) {
		this.type = type;
	}

	/**
	 * Get 'Basic' Authorization user name
	 * @return String
	 */
	@JsonProperty("username")
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set 'Basic' Authorization user name
	 * @param username
	 */
	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get 'Basic' Authorization password
	 * @return String
	 */
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}
	
	/**
	 *  Set 'Basic' Authorization password
	 * @param password
	 */
	@JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get 'OAuth' Authorization Access Token
	 * @return String
	 */
	@JsonProperty("accessToken")
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Set 'OAuth' Authorization Access Token
	 * @param accessToken
	 */
	@JsonProperty("accessToken")
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Get 'OAuth' Authorization API key
	 * @return String
	 */
	@JsonProperty("apiKey")
	public String getApiKey() {
		return apiKey;
	}
	
	/**
	 * Set 'OAuth' Authorization API key
	 * @param apiKey
	 */
	@JsonProperty("apiKey")
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	/**
	 * Get 'OAuth' Authorization API secret
	 * @return String
	 */
	@JsonProperty("apiSecret")
	public String getApiSecret() {
		return apiSecret;
	}
	
	/**
	 * Set 'OAuth' Authorization API secret
	 * @param apiSecret
	 */
	@JsonProperty("apiSecret")
	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}	
}