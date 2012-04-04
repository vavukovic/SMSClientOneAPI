package sms.common.model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.charset.GSMCharset;
import com.cloudhopper.smpp.SmppConstants;

public class SMS {
	private String senderAddress = "";
	private List<String> recipientsAddress = null;
	private String messageText = "";
	private String clientCorrelator = null;
	private String notifyURL = null;
	private String senderName = null;
	private String callbackData = null;
	private boolean sendAsFlashNotification = false;
	private String messageBinary = "";
	private int datacoding = 0;
	private int esmclass = 0;
	private int srcton = 0;
	private int srcnpi = 0;
	private int destton = 0;
	private int destnpi = 0;
	private int protocolid = 0;
	private String scheduleDeliveryTime = "";
	private String validityPeriod = "";
	private boolean encodeUnicodeTextToBinary = false;
	private boolean autoResolveSrcTonAndNpiOptions = false;
	private boolean autoResolveDestTonAndNpiOptions = false;

	public SMS() {
		super();
	}

	/**
	 * Initialize SMS object using mandatory parameters
	 * @param senderAddress
	 * @param recipientsAddress
	 * @param messageText
	 */
	public SMS(String senderAddress, String recipientsAddress, String messageText) {
		this.senderAddress = senderAddress;
		this.addRecipientAddress(recipientsAddress);
		this.setMessageText(messageText);
	}

	/**
	 * (mandatory) is the address to whom a responding SMS may be sent
	 * @return senderAddress
	 */
	public String getSenderAddress() {
		return senderAddress;
	}

	/**
	 * (mandatory) is the address to whom a responding SMS may be sent
	 * @param senderAddress
	 */
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	/**
	 * (mandatory) contains one address for end user ID to send to 
	 * @return recipientsAddress
	 */
	public List<String> getRecipientsAddress() {
		return recipientsAddress;
	}

	/**
	 * (mandatory) contains one address for end user ID to send to 
	 * @param recipientsAddress
	 */
	public void setRecipientsAddress(List<String> recipientsAddress) {
		this.recipientsAddress = recipientsAddress;
	}

	/**
	 * (mandatory) contains the message text to send
	 * @return
	 */
	public String getMessageText() {
		return messageText;
	}

	/**
	 * (mandatory) contains the message text to send
	 * @param messageText
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	/**
	 * (optional) uniquely identifies this create MMS request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid sending the same MMS twice.
	 * @return clientCorrelator
	 */
	public String getClientCorrelator() {
		return clientCorrelator;
	}

	/**
	 * (optional) uniquely identifies this create MMS request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid sending the same MMS twice.
	 * @param clientCorrelator
	 */
	public void setClientCorrelator(String clientCorrelator) {
		this.clientCorrelator = clientCorrelator;
	}

	/**
	 * (optional) is the URL to which you would like a notification of delivery sent
	 * @return notifyURL
	 */
	public String getNotifyURL() {
		return notifyURL;
	}

	/**
	 * (optional) is the URL to which you would like a notification of delivery sent
	 * @param notifyURL
	 */
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}

	/**
	 * (optional) is the name to appear on the user's terminal as the sender of the message
	 * @return senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * (optional) is the name to appear on the user's terminal as the sender of the message
	 * @param senderName
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * (optional) will be passed back to the notifyURL location, so you can use it to identify the message the receipt relates to (or any other useful data, such as a function name)
	 * @param callbackData
	 */
	public void setCallbackData(String callbackData) {
		this.callbackData = callbackData;
	}	

	/**
	 * (optional) will be passed back to the notifyURL location, so you can use it to identify the message the receipt relates to (or any other useful data, such as a function name)
	 * @return callbackData
	 */
	public String getCallbackData() {
		return callbackData;
	}

	/**
	 * (optional) determines if the SMS will be sent as 'Flash Notification'
	 * @return sendAsFlashNotification
	 */
	public boolean isSendAsFlashNotification() {
		return sendAsFlashNotification;
	}

	/**
	 * (optional) determines if the SMS will be sent as 'Flash Notification'
	 * @param sendAsFlashNotification
	 */
	public void setSendAsFlashNotification(boolean sendAsFlashNotification) {
		this.sendAsFlashNotification = sendAsFlashNotification;
	}

	/**
	 * (optional) SMS binary content
	 */
	public String getMessageBinary() {
		return messageBinary;
	}

	/**
	 * (optional) SMS binary content
	 * @param messageBinary
	 */
	public void setMessageBinary(String messageBinary) {
		this.messageBinary = messageBinary;
	}

	/**
	 * (optional) default value 0, (example: 8 = UNICODE data)
	 * @return datacoding
	 */
	public int getDatacoding() {
		return datacoding;
	}

	/**
	 * (optional) default value = 0, (example: 8 = UNICODE data)
	 * @param datacoding
	 */
	public void setDatacoding(int datacoding) {
		this.datacoding = datacoding;
	}

	/**
	 * (optional) Esm_class parameter, default value = 0 
	 * @return esmclass
	 */
	public int getEsmclass() {
		return esmclass;
	}

	/**
	 * (optional) Esm_class parameter, default value = 0 
	 * @param esmclass
	 */
	public void setEsmclass(int esmclass) {
		this.esmclass = esmclass;
	}

	/**
	 * (optional) Source - ton parameter
	 * @return srcton
	 */
	public int getSrcton() {
		return srcton;
	}

	/**
	 * (optional) Source - ton parameter
	 * @param srcton
	 */
	public void setSrcton(int srcton) {
		this.srcton = srcton;
	}

	/**
	 * (optional) Source - npi parameter
	 * @return srcnpi
	 */
	public int getSrcnpi() {
		return srcnpi;
	}

	/**
	 * (optional) Source - npi parameter
	 * @param srcnpi
	 */
	public void setSrcnpi(int srcnpi) {
		this.srcnpi = srcnpi;
	}

	/**
	 * (optional) Destination - ton parameter
	 * @return destton
	 */
	public int getDestton() {
		return destton;
	}

	/**
	 * (optional) Destination - ton parameter
	 * @param destton
	 */
	public void setDestton(int destton) {
		this.destton = destton;
	}

	/**
	 * (optional) Destination - npi parameter
	 * @return destnpi
	 */
	public int getDestnpi() {
		return destnpi;
	}

	/**
	 * (optional) Destination - npi parameter
	 * @param destnpi
	 */
	public void setDestnpi(int destnpi) {
		this.destnpi = destnpi;
	}

	/**
	 * (optional) Protocol Id parameter
	 * @return protocolid
	 */
	public int getProtocolid() {
		return protocolid;
	}

	/**
	 * (optional) Protocol Id parameter
	 * @param protocolid
	 */
	public void setProtocolid(int protocolid) {
		this.protocolid = protocolid;
	}

	/**
	 * (optional) Used for scheduled SMS (SMS not sent immediately but at scheduled time). “4d3h2m1s” means that message will be sent 4 days, 3 hours, 2 minutes and 1 second from now. You’re allowed to use any combination and leave out unnecessary variables.
	 * @return scheduleDeliveryTime
	 */
	public String getScheduleDeliveryTime() {
		return scheduleDeliveryTime;
	}

	/**
	 *  (optional) Used for scheduled SMS (SMS not sent immediately but at scheduled time). “4d3h2m1s” means that message will be sent 4 days, 3 hours, 2 minutes and 1 second from now. You’re allowed to use any combination and leave out unnecessary variables.
	 * @param scheduleDeliveryTime
	 */
	public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
		this.scheduleDeliveryTime = scheduleDeliveryTime;
	}

	/**
	 * (optional) ValidityPeriod pattern: HH:mm
	 * @return validityPeriod
	 */
	public String getValidityPeriod() {
		return validityPeriod;
	}

	/**
	 *  (optional) ValidityPeriod pattern: HH:mm
	 * @param validityPeriod
	 */
	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	/**
	 * Adds in the recipients list one address for end user ID to send to 
	 * @param recipientsAddress
	 */
	public void addRecipientAddress(String recipientsAddress) {
		if (this.recipientsAddress == null) {
			this.recipientsAddress = new ArrayList<String>();
		}

		this.recipientsAddress.add(recipientsAddress);
	}

	/**
	 * (optional) default = false, determines if the UNICODE text will be automatically converted to binary and 'Data Coding' parameter set to '8'
	 * @return encodeUnicodeTextToBinary
	 */
	public boolean isEncodeUnicodeTextToBinary() {
		return encodeUnicodeTextToBinary;
	}

	/**
	 * (optional) default = false, determines if the UNICODE text will be automatically converted to binary and 'Data Coding' parameter set to '8'
	 * @param encodeUnicodeTextToBinary
	 */
	public void setEncodeUnicodeTextToBinary(boolean encodeUnicodeTextToBinary) {
		this.encodeUnicodeTextToBinary = encodeUnicodeTextToBinary;
	}

	/**
	 * (optional) default = false, determines if 'Source-Ton' and 'Source-Npi' parameters will be automatically resolved depending on the 'Sender Address' value
	 * @return autoResolveSrcTonAndNpiOptions
	 */
	public boolean isAutoResolveSrcTonAndNpiOptions() {
		return autoResolveSrcTonAndNpiOptions;
	}

	/**
	 * (optional) default = false, determines if 'Source-Ton' and 'Source-Npi' parameters will be automatically resolved depending on the 'Sender Address' value
	 * @param autoResolveSrcTonAndNpiOptions
	 */
	public void setAutoResolveSrcTonAndNpiOptions(boolean autoResolveSrcTonAndNpiOptions) {
		this.autoResolveSrcTonAndNpiOptions = autoResolveSrcTonAndNpiOptions;
	}

	/**
	 * (optional) default = false, determines if 'Destination-Ton' and 'Destination-Npi' parameters will be automatically resolved depending on the 'Recipient Address' value
	 * @return autoResolveDestTonAndNpiOptions
	 */
	public boolean isAutoResolveDestTonAndNpiOptions() {
		return autoResolveDestTonAndNpiOptions;
	}

	/**
	 * (optional) default = false, determines if 'Destination-Ton' and 'Destination-Npi' parameters will be automatically resolved depending on the 'Recipient Address' value
	 * @param autoResolveDestTonAndNpiOptions
	 */
	public void setAutoResolveDestTonAndNpiOptions(boolean autoResolveDestTonAndNpiOptions) {
		this.autoResolveDestTonAndNpiOptions = autoResolveDestTonAndNpiOptions;
	}

	/**
	 * UNICODE text is automatically converted to binary and 'Data Coding' parameter set to '8'
	 * @throws UnsupportedEncodingException
	 */
	public void encodeUnicodeTextToBinary() throws UnsupportedEncodingException {
		byte[] textBytes = null;

		//if at least one character is not representable in GSM7, switch entire message to unicode.
		if (!GSMCharset.canRepresent(this.getMessageText())) {
			this.setDatacoding(SmppConstants.DATA_CODING_UCS2);
			textBytes = CharsetUtil.encode(this.getMessageText(), CharsetUtil.CHARSET_UCS_2);

			// fill binary instead of text
			StringBuffer hexStrBuff = new StringBuffer(textBytes.length * 2);
			for (int i = 0; i < textBytes.length; i++) {
				String hexByteStr = byteToHexDigit(textBytes[i]);
				hexStrBuff.append(hexByteStr);
			}

			this.setMessageBinary(hexStrBuff.toString());
			this.setMessageText("");
		}		
	}

	/**
	 * Resolve 'Source-Ton' and 'Source-Npi' parameters automatically depending on the 'Sender Address' value
	 */
	public void resolveSrcTonAndNpiOptions() {
		//set source TON, NPI values
		if ((!this.getSenderAddress().equals(null)) && (!this.getSenderAddress().isEmpty())) {
			if (isAlpha(this.getSenderAddress())) {
				this.setSrcton(SmppConstants.TON_ALPHANUMERIC);
				this.setSrcnpi(SmppConstants.NPI_UNKNOWN);
			} else if (this.getSenderAddress().startsWith("+")) {
				this.setSrcton(SmppConstants.TON_INTERNATIONAL);
				this.setSrcnpi(SmppConstants.NPI_E164);
			} else {
				this.setSrcton(SmppConstants.TON_NATIONAL);
				this.setSrcnpi(SmppConstants.NPI_E164);	
			}
		}
	}

	/**
	 * Resolve 'Destination-Ton' and 'Destination-Npi' parameters automatically depending on the 'Recipient Address' value
	 * @param destination
	 */
	public void resolveDestTonAndNpiOptions(String destination) {	
		//set destination TON, NPI values
		if ((!destination.equals(null)) && (!destination.isEmpty())) {
			if (destination.startsWith("+")) {
				this.setDestton(SmppConstants.TON_INTERNATIONAL);
			} else {
				this.setDestton(SmppConstants.TON_UNKNOWN);
			}
			this.setDestnpi(SmppConstants.NPI_UNKNOWN);
		}
	}

	private static String byteToHexDigit(byte theData) {
		String hexByteStr = Integer.toHexString(theData & 0xff).toUpperCase();
		if (hexByteStr.length() == 1) {
			hexByteStr = "0" + hexByteStr;
		}
		return hexByteStr;
	}

	private boolean isAlpha(String str) {
		if (str == null) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			if ((Character.isLetter(str.charAt(i)) == false) && ((str.charAt(i) != '+'))) {		
				return false; 
			}
		}
		return true;
	}	
}
