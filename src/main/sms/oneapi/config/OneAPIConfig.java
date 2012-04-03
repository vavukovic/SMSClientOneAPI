package sms.oneapi.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import sms.oneapi.model.Authorization;

@JsonSerialize(include = Inclusion.NON_NULL)
public class OneAPIConfig  {
	
	private String smsMessagingRootUrl = "";
	private String versionOneAPISMS = "1";
	private String retrieveInboundMessagesRegistrationId = "";
	private int inboundMessagesRetrievingInterval = 40000;
	private int dlrRetrievingInterval = 1000000;
	
	@JsonProperty("authorization")
	private Authorization authorization;
	
	/**
	 * Initialize configuration object 
	 */
	public OneAPIConfig() {  
		super();
		this.authorization = new Authorization();
	}
		
	/**
	 * Initialize configuration object using the 'Basic' Authorization
	 * @param smsMessagingRootUrl
	 * @param username
	 * @param password
	 */
	public OneAPIConfig(String smsMessagingRootUrl, String username, String password) {  
		this.smsMessagingRootUrl = smsMessagingRootUrl;
		this.authorization = new Authorization(username, password);	
	}
	
	/**
	 * Initialize configuration object using the 'OAuth' Authorization
	 * @param smsMessagingRootUrl
	 * @param accessToken
	 * @param apiKey
	 * @param apiSecret
	 */
	public OneAPIConfig(String smsMessagingRootUrl, String accessToken, String apiKey, String apiSecret) {  
		this.smsMessagingRootUrl = smsMessagingRootUrl;
		this.authorization = new Authorization(accessToken, apiKey, apiSecret);
	}

	@JsonProperty("authorization")
	public Authorization getAuthorization() {	
		return authorization;
	}

	public void setAuthorization(Authorization value) {
		this.authorization = value;
	}
		
	@JsonProperty("smsMessagingRootUrl")
	public String getSmsMessagingRootUrl() {
		return smsMessagingRootUrl;
	}

	public void setSmsMessagingRootUrl(String smsMessagingRootUrl) {
		this.smsMessagingRootUrl = smsMessagingRootUrl;
	}
		
	@JsonProperty("versionOneAPISMS")
	public String getVersionOneAPISMS() {
		return versionOneAPISMS;
	}

	public void setVersionOneAPISMS(String versionOneAPISMS) {
		this.versionOneAPISMS = versionOneAPISMS;
	}

	@JsonProperty("retrieveInboundMessagesRegistrationId")
	public String getRetrieveInboundMessagesRegistrationId() {
		return retrieveInboundMessagesRegistrationId;
	}

	public void setRetrieveInboundMessagesRegistrationId(String retrieveInboundMessagesRegistrationId) {
		this.retrieveInboundMessagesRegistrationId = retrieveInboundMessagesRegistrationId;
	}

	@JsonProperty("inboundMessagesRetrievingInterval")
	public int getInboundMessagesRetrievingInterval() {
		return inboundMessagesRetrievingInterval;
	}

	public void setInboundMessagesRetrievingInterval(int inboundMessagesRetrievingInterval) {
		this.inboundMessagesRetrievingInterval = inboundMessagesRetrievingInterval;
	}

	@JsonProperty("dlrRetrievingInterval")
	public int getDlrRetrievingInterval() {
		return dlrRetrievingInterval;
	}

	public void setDlrRetrievingInterval(int dlrRetrievingInterval) {
		this.dlrRetrievingInterval = dlrRetrievingInterval;
	}
}

