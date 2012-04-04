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

	/**
	 * SMPP server system id provided for each client
	 * @return systemId
	 */
	@JsonProperty("systemId")
	public String getSystemId() {
		return systemId;
	}

	/**
	 * SMPP server system id provided for each client
	 * @param systemId
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * SMPP server password provided for each client
	 * @return password
	 */
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	/**
	 * SMPP server password provided for each client
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * SMPP server host
	 * @return host
	 */
	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	/**
	 * SMPP server host
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * SMPP server port
	 * @return port
	 */
	@JsonProperty("port")
	public int getPort() {
		return port;
	}

	/**
	 * SMPP server port
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Determines if 'SMS' SMPP connection will be established when first DLR or INBOUND SMS listener will be added
	 * @return connectSMSSessionOnAddListener
	 */
	@JsonProperty("connectSMSSessionOnAddListener")
	public boolean getConnectSMSSessionOnAddListener() {
		return connectSMSSessionOnAddListener;
	}

	/**
	 * Determines if 'SMS' SMPP connection will be established when first DLR or INBOUND SMS listener is added
	 * @param connectSMSSessionOnAddListener
	 */
	public void setConnectSMSSessionOnAddListener(boolean connectSMSSessionOnAddListener) {
		this.connectSMSSessionOnAddListener = connectSMSSessionOnAddListener;
	}

	/**
	 * Determines if 'Flash Notification' SMPP connection will be established when first DLR or INBOUND SMS listener is added
	 * @return connectFlashSessionOnAddListener
	 */
	@JsonProperty("connectFlashSessionOnAddListener")
	public boolean getConnectFlashSessionOnAddListener() {
		return connectFlashSessionOnAddListener;
	}

	/**
	 * Determines if 'Flash Notification' SMPP connection will be established when first DLR or INBOUND SMS listener is added
	 * @param connectFlashSessionOnAddListener
	 */
	public void setConnectFlashSessionOnAddListener(boolean connectFlashSessionOnAddListener) {
		this.connectFlashSessionOnAddListener = connectFlashSessionOnAddListener;
	}

	/**
	 * Determines if 'HLR' SMPP connection will be established when first DLR or INBOUND SMS listener is added
	 * @return connectHLRSessionOnAddListener
	 */
	@JsonProperty("connectHLRSessionOnAddListener")
	public boolean getConnectHLRSessionOnAddListener() {
		return connectHLRSessionOnAddListener;
	}

	/**
	 * Determines if 'HLR' SMPP connection will be established when first DLR or INBOUND SMS listener is added
	 * @param connectHLRSessionOnAddListener
	 */
	public void setConnectHLRSessionOnAddListener(boolean connectHLRSessionOnAddListener) {
		this.connectHLRSessionOnAddListener = connectHLRSessionOnAddListener;
	}

	/**
	 * The amount of time to wait (in ms) before an unacknowledged request expires. -1 disables.
	 * @return requestExpiryTimeout
	 */
	@JsonProperty("requestExpiryTimeout")
	public int getRequestExpiryTimeout() {
		return requestExpiryTimeout;
	}

	/**
	 * The amount of time to wait (in ms) before an unacknowledged request expires. -1 disables.
	 * @param requestExpiryTimeout
	 */
	public void setRequestExpiryTimeout(int requestExpiryTimeout) {
		this.requestExpiryTimeout = requestExpiryTimeout;
	}

	/**
	 * Enquire_link interval in milliseconds. Enquire_link keeps the SMPP channel alive.
	 * @return keepAliveInterval
	 */
	@JsonProperty("keepAliveInterval")
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * Enquire_link interval in milliseconds. Enquire_link keeps the SMPP channel alive.
	 * @param keepAliveInterval
	 */
	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	/**
	 * Indicates the type of service associated with the message
	 * @return systemType
	 */
	@JsonProperty("systemType")
	public String getSystemType() {
		return systemType;
	}

	/**
	 * Indicates the type of service associated with the message
	 * @param systemType
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	/**
	 * Connection Timeout in milliseconds
	 * @return connectionTimeout
	 */
	@JsonProperty("connectionTimeout")
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Connection Timeout in milliseconds
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * SMPP Session name
	 * @return
	 */
	@JsonProperty("smppName")
	public String getSmppName() {
		return smppName;
	}

	/**
	 * SMPP Session name
	 * @param smppName
	 */
	public void setSmppName(String smppName) {
		this.smppName = smppName;
	}

	/**
	 * The amount of time to wait (in ms) between executions of monitoring the window. Must be 50%-100% of the requestExpiryTimeout value
	 * @return windowMonitorInterval
	 */
	@JsonProperty("windowMonitorInterval")
	public int getWindowMonitorInterval() {
		return windowMonitorInterval;
	}

	/**
	 * The amount of time to wait (in ms) between executions of monitoring the window. Must be 50%-100% of the requestExpiryTimeout value
	 * @param windowMonitorInterval
	 */
	public void setWindowMonitorInterval(int windowMonitorInterval) {
		this.windowMonitorInterval = windowMonitorInterval;
	}

	/**
	 * Flag indicating if the message is a registered short message and thus if a Delivery Receipt is required upon the message attaining a final state. 0=No receipt required (non-registered delivery). 1=Receipt required (registered delivery)
	 * @return registeredDelivery
	 */
	@JsonProperty("registeredDelivery")
	public byte getRegisteredDelivery() {
		return registeredDelivery;
	}

	/**
	 * Flag indicating if the message is a registered short message and thus if a Delivery Receipt is required upon the message attaining a final state. 0=No receipt required (non-registered delivery). 1=Receipt required (registered delivery)
	 * @param registeredDelivery
	 */
	public void setRegisteredDelivery(byte registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}	
}
