package sms.oneapi.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import sms.oneapi.model.Authorization;

@JsonSerialize(include = Inclusion.NON_NULL)
public class OneAPIConfig  {

	private String smsMessagingBaseUrl = "";
	private String versionOneAPISMS = "1";
	private String retrieveInboundMessagesRegistrationId = "";
	private int inboundMessagesRetrievingInterval = 40000;
	private int dlrRetrievingInterval = 1000000;
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
		this.smsMessagingBaseUrl = smsMessagingRootUrl;
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
		this.smsMessagingBaseUrl = smsMessagingRootUrl;
		this.authorization = new Authorization(accessToken, apiKey, apiSecret);
	}

	/**
	 * Object containing 'OneAPI' authorization data
	 * @return authorization
	 */
	@JsonProperty("authorization")
	public Authorization getAuthorization() {	
		return authorization;
	}

	/**
	 * Object containing 'OneAPI' authorization data
	 * @param value
	 */
	public void setAuthorization(Authorization value) {
		this.authorization = value;
	}

	/**
	 * Base URL containing host name and port of the OneAPI SMS server
	 * @return smsMessagingBaseUrl
	 */
	@JsonProperty("smsMessagingBaseUrl")
	public String getSmsMessagingBaseUrl() {
		return smsMessagingBaseUrl;
	}

	/**
	 * Base URL containing host name and port of the OneAPI SMS server
	 * @param smsMessagingBaseUrl
	 */
	public void setSmsMessagingBaseUrl(String smsMessagingBaseUrl) {
		this.smsMessagingBaseUrl = smsMessagingBaseUrl;
	}

	/**
	 * Version of OneAPI SMS you are accessing (the default is the latest version supported by that server)
	 * @return versionOneAPISMS
	 */
	@JsonProperty("versionOneAPISMS")
	public String getVersionOneAPISMS() {
		return versionOneAPISMS;
	}

	/**
	 * Version of OneAPI SMS you are accessing (the default is the latest version supported by that server)
	 * @param versionOneAPISMS
	 */
	public void setVersionOneAPISMS(String versionOneAPISMS) {
		this.versionOneAPISMS = versionOneAPISMS;
	}

	/**
	 * Registration ID agreed with the OneAPI operator used to retrieve INBOUND SMS messages 
	 * @return retrieveInboundMessagesRegistrationId
	 */
	@JsonProperty("retrieveInboundMessagesRegistrationId")
	public String getRetrieveInboundMessagesRegistrationId() {
		return retrieveInboundMessagesRegistrationId;
	}

	/**
	 * Registration ID agreed with the OneAPI operator used to retrieve INBOUND SMS messages 
	 * @param retrieveInboundMessagesRegistrationId
	 */
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

