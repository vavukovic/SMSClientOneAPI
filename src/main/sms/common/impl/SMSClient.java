package sms.common.impl;

import sms.common.config.MainConfig;
import sms.common.exceptiontype.ConfigException;
import sms.common.exceptiontype.CreateSmsException;
import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.InboundMessageListenerException;
import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.SendHlrRequestException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.SMS;
import sms.common.response.SMSSendResponse;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.impl.OneAPIImpl;
import sms.smpp.config.SmppConfig;

import com.cloudhopper.commons.charset.GSMCharset;

public class SMSClient extends OneAPIImpl implements Sender  {

	//Contains methods to send SMS messages. Configuration property 'senderType' determines if it the 'sender' will be 'OneAPI' or 'SMPP' type.
	private Sender sender = null;
	//Used to resolve 'sender' object type ('OneAPI', 'SMPP')
	private SenderFactory factory = new SenderFactory();

	//*************************SMSClient initialization***********************************************************************************************************************************************
	/**
	 * Initialize SMS client and load data from the 'client.cfg' configuration file 
	 * @see Configuration property 'senderType' determines if the SMS messages will be send over the 'OneAPI' or 'SMPP' protocol
	 * @throws ConfigException 
	 */
	public SMSClient() throws ConfigException {	
		//Initialize main configuration
		MainConfig mainConfig = new MainConfig();
		//Load data from the configuration 'client.cfg' file
		mainConfig.loadFromConfigFile();

		//OneAPI configuration used in extended 'OneAPIImpl' class
		this.oneAPIConfig = mainConfig.getOneAPI();    
		//Create 'sender' object used to send SMS messages depending on the configuration "SenderType" property
		this.sender = this.factory.CreateSender(mainConfig.getSenderType(), this.oneAPIConfig, mainConfig.getSmpp());		
	}

	/**
	 * Initialize SMS client using specified 'oneAPIConfig' parameter 
	 * @see SMS messages are send over the 'OneAPI' protocol
	 * @param oneAPIConfig - parameter containing OneAPI configuration data
	 * @throws ConfigException 
	 */
	public SMSClient(OneAPIConfig oneAPIConfig) throws ConfigException {
		//OneAPI configuration used in extended 'OneAPIImpl' class
		this.oneAPIConfig = oneAPIConfig;
		//Create OneAPI 'sender' object used to send SMS messages
		this.sender = this.factory.createOneAPISender(this.oneAPIConfig);	
	}

	/**
	 * Initialize OneAPI client using specified 'oneAPIConfig', 'senderConfig' parameters 
	 * @see SMS messages are send over the SMPP protocol
	 * @param oneAPIConfig - parameter containing OneAPI configuration data
	 * @param senderConfig - parameter containing SMPP configuration data
	 * @throws ConfigException 
	 */
	public SMSClient(OneAPIConfig oneAPIConfig, SmppConfig senderConfig) throws ConfigException {
		//OneAPI configuration used in extended 'OneAPIImpl' class
		this.oneAPIConfig = oneAPIConfig;
		//Create SMPP 'sender' object used to send SMS messages
		this.sender = this.factory.createSMPPSender(senderConfig);
	}


	//*************************SMSClient public***********************************************************************************************************************************************
	/**
	 * Supported sender types
	 */
	public enum SenderType {
		ONEAPI, SMPP;
	};

	/**
	 *  Send an SMS to one or more mobile terminals using the customized 'SMS' object
	 * @param sms - object containing data needed to be filled in order to send the SMS
	 * @return SMSSendResponse
	 * @throws SendSmsException
	 */
	@Override
	public SMSSendResponse sendSMS(SMS sms) throws SendSmsException {
		return this.sender.sendSMS(sms);
	}

	/**
	 * Send an SMS to one mobile terminal using mandatory parameters 
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws CreateSmsException 
	 * @throws SendSmsException
	 */
	@Override
	public SMSSendResponse sendSMS(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException {
		return this.sender.sendSMS(senderAddress, recipientAddress, messageText);	
	}
	/**
	 * Add 'Delivery Reports' listener
	 * @param listener - (new DeliveryReportListener)
	 */
	@Override
	public void addDeliveryReportListener(DeliveryReportListener listener) throws DeliveryReportListenerException {
		this.sender.addDeliveryReportListener(listener);
	}

	/**
	 * Add 'Inbound Messages' listener
	 * @see OneAPI - Messages are pulled automatically depending on the 'inboundMessagesRetrievingInterval' client configuration parameter, SMPP - messages are retrieved on one of the opened SMPP sessions
	 * @param listener - (new InboundMessageListener)
	 * @throws InboundMessageListenerException 
	 */
	@Override
	public void addInboundMessageListener(InboundMessageListener listener) throws InboundMessageListenerException {
		this.sender.addInboundMessageListener(listener);	
	}
	//
	/**
	 * Release client resources 
	 */
	@Override
	public void destroy() {
		this.sender.destroy();
	}

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
	@Override
	public SMSSendResponse sendScheduledSMS(String senderAddress, String recipientAddress, String messageText, String scheduleDeliveryTime) throws CreateSmsException, SendSmsException, NotSupportedException {	
		return this.sender.sendFlashNotification(senderAddress, recipientAddress, messageText);
	}

	/**
	 * Send an 'Flash Notification' SMS to one mobile terminal ('setSendAsFlashNotification'property is automatically set to 'True') 
	 * @See In case the specified 'messageText' is 'UNICODE' it is encoded to binary and 'DataCoding' parameter is automatically set to '8'. 'SrcTon', 'SrcNpi', 'DestTon', 'DestNpi' message parameters are resolved automatically depending on the specified 'senderAddress' and 'recipientAddress')
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendFlashNotification(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException, NotSupportedException {		
		return this.sender.sendFlashNotification(senderAddress, recipientAddress, messageText);
	}

	/**
	 * Send HLR Request for one mobile terminal
	 * @param destination (mandatory) is the address for whom HLR request is send
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendHLRRequest(String destination) throws SendHlrRequestException, NotSupportedException {		
		return this.sender.sendHLRRequest(destination);
	}

	/**
	 * Get sender type (ONEAPI, SMPP)
	 * @return SenderType
	 */
	@Override
	public SenderType getSenderType() {
		return this.sender.getSenderType();
	}

	/**
	 * Checks if specified text is 'UNICODE'
	 * @param text
	 * @return 'True' if specified text is 'UNICODE', else 'False'
	 */
	public boolean isUnicode(String text) {
		return (!GSMCharset.canRepresent(text));
	}
}
