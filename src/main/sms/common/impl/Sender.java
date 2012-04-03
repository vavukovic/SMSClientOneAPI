package sms.common.impl;

import sms.common.exceptiontype.CreateSmsException;
import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.InboundMessageListenerException;
import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.SendHlrRequestException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.impl.SMSClient.SenderType;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.SMS;
import sms.common.response.SMSSendResponse;

public interface Sender {
	
	/**
	 * Send an SMS to one or more mobile terminals using the customized SMS object 
	 * @param sms - object containing data needed to be filled in order to send the SMS
	 * @return SMSSendResponse
	 * @throws SendSmsException
	 */
	public SMSSendResponse sendSMS(SMS sms) throws SendSmsException;
	
	/**
	 * Send an SMS to one mobile terminal using mandatory parameters 
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws CreateSmsException 
	 * @throws SendSmsException 
	 */
	public SMSSendResponse sendSMS(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException;

	/**
	 * Send an 'Scheduled' SMS to one mobile terminal (SMS is not sent immediately but at scheduled time)
	 * @See In case the specified 'messageText' is 'UNICODE' it is encoded to binary and 'DataCoding' parameter is automatically set to '8'. 'SrcTon', 'SrcNpi', 'DestTon', 'DestNpi' message parameters are resolved automatically depending on the specified 'senderAddress' and 'recipientAddress')
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @param scheduleDeliveryTime (mandatory) - Example: format “YYMMDDhhmmsstnnp" - “000011060755000R“ means 11 days, 6 hours, 7 minutes, 55 seconds from now.
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	public SMSSendResponse sendScheduledSMS(String senderAddress, String recipientAddress, String messageText, String scheduleDeliveryTime) throws CreateSmsException, SendSmsException, NotSupportedException;
	
	/**
	 * Send an 'Flash Notification' SMS to one mobile terminal ('setSendAsFlashNotification'property is automatically set to 'True') 
	 * @See In case the specified 'messageText' is 'UNICODE' it is encoded to binary and 'DataCoding' parameter is automatically set to '8'. 'SrcTon', 'SrcNpi', 'DestTon', 'DestNpi' message parameters are resolved automatically depending on the specified 'senderAddress' and 'recipientAddress')
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	public SMSSendResponse sendFlashNotification(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException, NotSupportedException;
			
	/**
	 * Send HLR Request for one mobile terminal
	 * @param destination (mandatory) is the address for whom HLR request is send
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	public SMSSendResponse sendHLRRequest(String destination) throws SendHlrRequestException, NotSupportedException;
		
	/**
	 * Add 'Inbound Messages' listener
	 * 
	 * @param listener - (new InboundMessageListener)
	 * @throws InboundMessageListenerException 
	 */
	public void addInboundMessageListener(InboundMessageListener listener) throws InboundMessageListenerException;
			
	/**
	 * Add 'Delivery Reports' listener.
	 * @param listener - (new DeliveryReportListener)
	 * @throws DeliveryReportListenerException 
	 */
	public void addDeliveryReportListener(DeliveryReportListener listener) throws DeliveryReportListenerException;
	
	/**
	 * Release client resources
	 */
	public void destroy();
	
	/**
	 * Get sender type (ONEAPI, SMPP)
	 * @return SenderType
	 */
	public SenderType getSenderType();
}
