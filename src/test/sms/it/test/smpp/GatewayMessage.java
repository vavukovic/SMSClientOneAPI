package sms.it.test.smpp;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.infobip.commonweb.util.HexStringUtils;
import org.infobip.ipdatamodel.SmsMessageType;

public class GatewayMessage {
	public static final Pattern UDH_PATTERN = Pattern.compile("^050003([0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F])[0-9A-F]*$");

	private String id = "";
	private String content = "";
	private String sender = "";
	private String recipient = "";
	private Integer smsChannelId = null;
	private int dataCoding = 1;
	private int esmClass = 0;

	private byte[] binaryContent;
	private UserDataHeader userDataHeader;
	private byte[] binaryMessageContent;

	private Integer internalLogId;
	private Integer internalLogSmsMessageContentId;
	private Integer contentSmsMessageContentId;
	private Integer messageQueueId;
	private Integer statusId;
	private Integer count;
	private Integer networkId;
	private double pricePerMessage;
	private double creditsPerMessage;
	private String externalMessageId;

	private byte srcTon;
	private byte srcNpi;
	private byte destTon;
	private byte destNpi;
    private SmsMessageType smsMessageType = SmsMessageType.MESSAGE_TYPE_NORMAL;
    private boolean doRemap;

	private String clientAppId;
	
	private int ussdSessionId;
	private int ussdOpId;
	
	private String servingMsc;
	private String servingHlr;
	private String imsi;

    public GatewayMessage(String id, String content, String sender, String recipient, Integer smsChannelId) {
		this.id = id;
		this.content = content;
		this.sender = sender;
		this.recipient = recipient;
		this.smsChannelId = smsChannelId;
	}

	public GatewayMessage(String id, byte[] binaryContent, String sender, String recipient, Integer smsChannelId) {
		this.id = id;
		userDataHeader = createUserDataHeader(binaryContent);
		this.binaryContent = binaryContent;
		this.binaryMessageContent = createMessageContent(binaryContent);
		this.sender = sender;
		this.recipient = recipient;
		this.smsChannelId = smsChannelId;
	}

	public GatewayMessage(String id, byte[] binaryContent, String sender, String recipient, Integer smsChannelId, byte sourceTon, byte sourceNpi, byte destinationTon, byte destinationNpi) {
		this(id, binaryContent, sender, recipient, smsChannelId);
		this.srcTon = sourceTon;
		this.srcNpi = sourceNpi;
		this.destTon = destinationTon;
		this.destNpi = destinationNpi;
	}

	private byte[] createMessageContent(byte[] binaryContent) {
		if (null != userDataHeader) {
			return HexStringUtils.hexStringToBytes(HexStringUtils.bytesToHexString(binaryContent).substring(12));
		}
		return binaryContent;
	}

	private UserDataHeader createUserDataHeader(byte[] binaryContent) {
		if (binaryContent.length < 6) {
			return null;
		}

		Matcher matcher = UDH_PATTERN.matcher(HexStringUtils.bytesToHexString(binaryContent));

		if (!matcher.find()) {
			return null;
		}

		byte[] udh = HexStringUtils.convertFromHexArray(matcher.group(1));
		return new UserDataHeader(new byte[] { udh[0] }, udh[1], udh[2]);
	}

	public String getId() {
		return id;
	}

	public byte[] getBinaryContent() {
		return binaryContent;
	}

	public UserDataHeader getUserDataHeader() {
		return userDataHeader;
	}

	public byte[] getBinaryMessageContent() {
		return binaryMessageContent;
	}

	@Override
	public String toString() {
		return "GatewayMessage{ " + "id = '" + id + '\'' + ", content = " + content + ", binaryContent = " + binaryContent + ", userDataHeader = " + userDataHeader + ", binaryMessageContent = " + binaryMessageContent + " }";
	}

	public String getContent() {
		return content;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setInternalLogId(Integer internalLogId) {
		this.internalLogId = internalLogId;
	}

	public Integer getInternalLogId() {
		return internalLogId;
	}

	public void setInternalLogSmsMessageContentId(Integer internalLogSmsMessageContentId) {
		this.internalLogSmsMessageContentId = internalLogSmsMessageContentId;
	}

	public Integer getInternalLogSmsMessageContentId() {
		return internalLogSmsMessageContentId;
	}

	public void setContentSmsMessageContentId(Integer contentSmsMessageContentId) {
		this.contentSmsMessageContentId = contentSmsMessageContentId;
	}

	public Integer getContentSmsMessageContentId() {
		return contentSmsMessageContentId;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setSmsChannelId(Integer smsChannelId) {
		this.smsChannelId = smsChannelId;
	}

	public Integer getSmsChannelId() {
		return smsChannelId;
	}

	public void setDataCoding(int dataCoding) {
		this.dataCoding = dataCoding;
	}

	public int getDataCoding() {
		return dataCoding;
	}

	public void setEsmClass(int esmClass) {
		this.esmClass = esmClass;
	}

	public int getEsmClass() {
		return esmClass;
	}

	public void setMessageQueueId(Integer messageQueueId) {
		this.messageQueueId = messageQueueId;
	}

	public Integer getMessageQueueId() {
		return messageQueueId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}

	public Integer getNetworkId() {
		return networkId;
	}

	public double getPricePerMessage() {
		return pricePerMessage;
	}

	public void setPricePerMessage(double pricePerMessage) {
		this.pricePerMessage = pricePerMessage;
	}

	public double getCreditsPerMessage() {
		return creditsPerMessage;
	}

	public void setCreditsPerMessage(double creditsPerMessage) {
		this.creditsPerMessage = creditsPerMessage;
	}

	public String getExternalMessageId() {
		return externalMessageId;
	}

	public void setExternalMessageId(String externalMessageId) {
		this.externalMessageId = externalMessageId;
	}

	public byte getSrcTon() {
		return srcTon;
	}

	public void setSrcTon(byte srcTon) {
		this.srcTon = srcTon;
	}

	public byte getSrcNpi() {
		return srcNpi;
	}

	public void setSrcNpi(byte srcNpi) {
		this.srcNpi = srcNpi;
	}

	public byte getDestTon() {
		return destTon;
	}

	public void setDestTon(byte destTon) {
		this.destTon = destTon;
	}

	public byte getDestNpi() {
		return destNpi;
	}

	public void setDestNpi(byte destNpi) {
		this.destNpi = destNpi;
	}

	public void setBinaryContent(byte[] binaryContent) {
		this.binaryContent = binaryContent;
	}

    public SmsMessageType getSmsMessageType() {
        return smsMessageType;
    }

    public void setSmsMessageType(SmsMessageType smsMessageType) {
        this.smsMessageType = smsMessageType;
    }

    public boolean isDoRemap() {
        return doRemap;
    }

    public void setDoRemap(boolean doRemap) {
        this.doRemap = doRemap;
    }

	public String getClientAppId() {
		return clientAppId;
	}

	public void setClientAppId(String clientAppId) {
		this.clientAppId = clientAppId;
	}

	public int getUssdSessionId() {
		return ussdSessionId;
	}

	public void setUssdSessionId(int ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}

	public int getUssdOpId() {
		return ussdOpId;
	}

	public void setUssdOpId(int ussdOpId) {
		this.ussdOpId = ussdOpId;
	}
	
	public void setServingHlr(String servingHlr) {
		this.servingHlr = servingHlr;
	}
	
	public String getServingHlr() {
		return servingHlr;
	}
	
	public void setServingMsc(String servingMsc) {
		this.servingMsc = servingMsc;
	}
	
	public String getServingMsc() {
		return servingMsc;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
