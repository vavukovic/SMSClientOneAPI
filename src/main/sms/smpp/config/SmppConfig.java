package sms.smpp.config;

import org.codehaus.jackson.annotate.JsonProperty;
import com.cloudhopper.smpp.SmppConstants;

public class SmppConfig {
	
	private String systemId = "";
	private String password = "";
	private String host = "";
	private int port = 0;
	private boolean connectSMSSessionOnAddListener = true;
	private boolean connectFlashSessionOnAddListener = false;
	private boolean connectHLRSessionOnAddListener = false;
	private int requestExpiryTimeout = 30000;
	private int keepAliveInterval = 30000;
	private String systemType = "";
	private int connectionTimeout = 10000;
	private String smppName = "Client.Session.0";
	private int windowMonitorInterval = 15000;
	private byte registeredDelivery = SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED;

	/**
	 * Initialize configuration object 
	 */
	public SmppConfig() {  
		super();
	}
	
	/**
	 * Initialize configuration object using SMPP server connection parameters
	 * @param systemId
	 * @param password
	 * @param host
	 * @param port
	 */
	public SmppConfig(String systemId, String password, String host, int port) {  
		this.systemId = systemId;
		this.password = password;
		this.host = host;
		this.port = port;
	}
	
	@JsonProperty("systemId")
	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("port")
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@JsonProperty("connectSMSSessionOnAddListener")
	public boolean getConnectSMSSessionOnAddListener() {
		return connectSMSSessionOnAddListener;
	}

	public void setConnectSMSSessionOnAddListener(boolean connectSMSSessionOnAddListener) {
		this.connectSMSSessionOnAddListener = connectSMSSessionOnAddListener;
	}

	@JsonProperty("connectFlashSessionOnAddListener")
	public boolean getConnectFlashSessionOnAddListener() {
		return connectFlashSessionOnAddListener;
	}

	public void setConnectFlashSessionOnAddListener(boolean connectFlashSessionOnAddListener) {
		this.connectFlashSessionOnAddListener = connectFlashSessionOnAddListener;
	}

	@JsonProperty("connectHLRSessionOnAddListener")
	public boolean getConnectHLRSessionOnAddListener() {
		return connectHLRSessionOnAddListener;
	}
	public void setConnectHLRSessionOnAddListener(boolean connectHLRSessionOnAddListener) {
		this.connectHLRSessionOnAddListener = connectHLRSessionOnAddListener;
	}

	@JsonProperty("requestExpiryTimeout")
	public int getRequestExpiryTimeout() {
		return requestExpiryTimeout;
	}

	public void setRequestExpiryTimeout(int requestExpiryTimeout) {
		this.requestExpiryTimeout = requestExpiryTimeout;
	}

	@JsonProperty("keepAliveInterval")
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	@JsonProperty("systemType")
	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	@JsonProperty("connectionTimeout")
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	@JsonProperty("smppName")
	public String getSmppName() {
		return smppName;
	}

	public void setSmppName(String smppName) {
		this.smppName = smppName;
	}

	@JsonProperty("windowMonitorInterval")
	public int getWindowMonitorInterval() {
		return windowMonitorInterval;
	}

	public void setWindowMonitorInterval(int windowMonitorInterval) {
		this.windowMonitorInterval = windowMonitorInterval;
	}

	@JsonProperty("registeredDelivery")
	public byte getRegisteredDelivery() {
		return registeredDelivery;
	}

	public void setRegisteredDelivery(byte registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}	
}
