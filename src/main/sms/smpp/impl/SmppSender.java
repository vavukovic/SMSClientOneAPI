package sms.smpp.impl;

import javax.swing.event.EventListenerList;

import sms.common.exceptiontype.CreateSmsException;
import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.InboundMessageListenerException;
import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.SendHlrRequestException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.impl.Sender;
import sms.common.impl.SMSClient.SenderType;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.SMS;
import sms.common.response.SMSSendResponse;
import sms.smpp.config.SmppConfig;
import sms.smpp.exceptiontype.SmppClientBootstrapException;
import sms.smpp.exceptiontype.SmppEstablishConnectionException;

public class SmppSender implements Sender {
	private SmppConfig smppConfig = null;
	private SmppSessionWrapper smsSession = null;
	private SmppSessionWrapper hlrRequestSession = null;
	private SmppSessionWrapper flashNotificationSession = null;
	private DefaultClientBootstrap defaultClientBootstrap = new DefaultClientBootstrap();
	public static final String FLASH_NOTIFICATION_SYSTEM_TYPE = "NSMS";
	public static final String HLR_REQUEST_SYSTEM_TYPE = "HLR";
	private EventListenerList deliveryReportListenerList = null;
	private EventListenerList inboundMessageListenerList = null;

	//*************************SmppSender initialization***********************************************************************************************************************************************
	public SmppSender(SmppConfig smppConfig) {
		this.smppConfig = smppConfig;
	}

	//*************************SmppSender public*******************************************************************************************************************************************************	
	/**
	 * Send an SMS over SMPP protocol to one or more mobile terminals using the customized 'SMS' object 
	 * @param sms - object containing data needed to be filled in order to send the SMS
	 * @return SMSSendResponse
	 * @throws SendSmsException
	 */
	@Override
	public SMSSendResponse sendSMS(SMS sms) throws SendSmsException {
		if (sms == null) throw new SendSmsException("'sms' parameter is null");

		SmppSessionWrapper currentSession = null;

		try {
			if (sms.isSendAsFlashNotification()) {
				this.createFlashNotificationSession();
				currentSession = this.flashNotificationSession;
			} else {
				this.createSmsSession();
				currentSession = this.smsSession;
			}

			return currentSession.submitSm(sms);
		} catch (Exception e) {
			throw new SendSmsException(e.getMessage(), e);
		}	
	}

	/**
	 * Send an SMS over SMPP protocol to one mobile terminal using mandatory parameters 
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws CreateSmsException 
	 * @throws SendSmsException 
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendSMS(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException {
		SMS sms = this.createSMS(senderAddress, recipientAddress, messageText);
		return this.sendSMS(sms);
	}

	/**
	 * Send an 'Scheduled' SMS over SMPP protocol to one mobile terminal (SMS is not sent immediately but at scheduled time) 
	 * @See In case the specified 'messageText' is 'Unicode' it is encoded to binary and 'DataCoding' parameter is automatically set to '8'. 'SrcTon', 'SrcNpi', 'DestTon', 'DestNpi' message parameters are resolved automatically depending on the specified 'senderAddress' and 'recipientAddress')
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @param scheduleDeliveryTime (mandatory) - Example: format “YYMMDDhhmmsstnnp" - “000011060755000R“ means 11 days, 6 hours, 7 minutes, 55 seconds from now.
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendScheduledSMS(String senderAddress, String recipientAddress, String messageText, String scheduleDeliveryTime) throws CreateSmsException, SendSmsException, NotSupportedException {	
		SMS sms = this.createSMS(senderAddress, recipientAddress, messageText);
		sms.setScheduleDeliveryTime(scheduleDeliveryTime);

		try {
			return this.sendSMS(sms);
		} catch (Exception e) {
			throw new SendSmsException(e.getMessage(), e);
		}
	}

	/**
	 * Send an 'Flash Notification' SMS over SMPP protocol to one mobile terminal ('setSendAsFlashNotification'property is automatically set to 'True') 
	 * @See In case the specified 'messageText' is 'Unicode' it is encoded to binary and 'DataCoding' parameter is automatically set to '8'. 'SrcTon', 'SrcNpi', 'DestTon', 'DestNpi' message parameters are resolved automatically depending on the specified 'senderAddress' and 'recipientAddress')
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send   
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendFlashNotification(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException, SendSmsException, NotSupportedException {		
		SMS sms = this.createSMS(senderAddress, recipientAddress, messageText);
		sms.setSendAsFlashNotification(true);
		return this.sendSMS(sms);	
	}

	/**
	 * Send HLR Request for one mobile terminal over SMPP protocol  
	 * @param destination (mandatory) is the address for whom HLR request is send
	 * @return SMSSendResponse
	 * @throws NotSupportedException 
	 */
	@Override
	public SMSSendResponse sendHLRRequest(String destination) throws SendHlrRequestException, NotSupportedException {		
		try {
			this.createHlrSession();		
			return hlrRequestSession.submitSm(destination);
		} catch (Exception e) {
			throw new SendHlrRequestException (e.getMessage(), e);
		}
	}

	/**
	 * Add SMPP 'Delivery Reports' listener.
	 * @param listener - (new DeliveryReportListenerSmpp)
	 * @throws NotSupportedException 
	 */
	@Override
	public void addDeliveryReportListener(DeliveryReportListener listener) throws DeliveryReportListenerException {
		if (this.deliveryReportListenerList == null) {
			this.deliveryReportListenerList = new EventListenerList();
		}		
		this.deliveryReportListenerList.add(DeliveryReportListener.class, listener);

		try {
			this.CreateSessions();
		} catch (Exception e) {
			throw new DeliveryReportListenerException(e.getMessage(), e);
		}
	}

	/**
	 * Add SMPP 'INBOUND Messages' listener
	 * @param listener - (new InboundMessageListener)
	 * @throws InboundMessageListenerException  
	 */
	@Override
	public void addInboundMessageListener(InboundMessageListener listener) throws InboundMessageListenerException {
		if (this.inboundMessageListenerList == null) {
			this.inboundMessageListenerList = new EventListenerList();
		}	
		this.inboundMessageListenerList.add(InboundMessageListener.class, listener);

		try {
			this.CreateSessions();
		} catch (Exception e) {
			throw new InboundMessageListenerException(e.getMessage(), e);
		}
	}

	/**
	 * Release SmppSender resources (unbind all sessions and release listeners)
	 */
	@Override
	public void destroy() {
		if (this.smsSession != null) {
			this.smsSession.unbind();	
			this.smsSession = null;
		}

		if (this.hlrRequestSession != null) {
			this.hlrRequestSession.unbind();			
			this.hlrRequestSession = null;
		}

		if (this.flashNotificationSession != null) {
			this.flashNotificationSession.unbind();			
			this.flashNotificationSession = null;
		}
	}

	/**
	 * Get sender type
	 * @return SenderType
	 */
	@Override
	public SenderType getSenderType() {
		return SenderType.SMPP;
	}

	//*************************SmppSender private*************************************************************************************************************************************************	
	
	/**
	 * Create sessions depending on the SMPP configuration flags
	 * @throws SmppClientBootstrapException
	 * @throws SmppEstablishConnectionException
	 */
	private void CreateSessions() throws SmppClientBootstrapException, SmppEstablishConnectionException {
		if (smppConfig.getConnectSMSSessionOnAddListener() == true) {
			this.createSmsSession();	
		}

		if (smppConfig.getConnectFlashSessionOnAddListener() == true) {
			this.createFlashNotificationSession();
		}

		if (smppConfig.getConnectHLRSessionOnAddListener() == true) {
			this.createHlrSession();	
		}
	}

	/**
	 * Create 'SMS' Session
	 * @throws SmppClientBootstrapException
	 * @throws SmppEstablishConnectionException
	 */
	private void createSmsSession() throws SmppClientBootstrapException, SmppEstablishConnectionException {
		if ((this.smsSession == null) || (!this.smsSession.isInitialized())) {
			this.smsSession = this.createSession();
			this.smsSession.setSystemType(this.smppConfig.getSystemType());
			this.smsSession.bind(this.smppConfig);
		}

		this.smsSession.setDeliveryReportListenerList(deliveryReportListenerList);
		this.smsSession.setIncomingMessageListenerList(inboundMessageListenerList);
	}

	/**
	 * Create 'Flash Notification' Session
	 */
	private void createFlashNotificationSession() throws SmppClientBootstrapException, SmppEstablishConnectionException {
		if ((this.flashNotificationSession == null) || (!this.flashNotificationSession.isInitialized())) {
			this.flashNotificationSession = this.createSession();
			this.flashNotificationSession.setSystemType(FLASH_NOTIFICATION_SYSTEM_TYPE);
			this.flashNotificationSession.bind(this.smppConfig);
		}

		this.flashNotificationSession.setDeliveryReportListenerList(deliveryReportListenerList);
		this.flashNotificationSession.setIncomingMessageListenerList(inboundMessageListenerList);
	}

	/**
	 * Create 'HLR' Session
	 * @throws SmppClientBootstrapException
	 * @throws SmppEstablishConnectionException
	 */
	private void createHlrSession() throws SmppClientBootstrapException, SmppEstablishConnectionException {
		if ((this.hlrRequestSession == null) || (!this.hlrRequestSession.isInitialized())) {
			this.hlrRequestSession = this.createSession();
			this.hlrRequestSession.setSystemType(HLR_REQUEST_SYSTEM_TYPE);
			this.hlrRequestSession.bind(this.smppConfig);
		}

		this.hlrRequestSession.setDeliveryReportListenerList(deliveryReportListenerList);
		this.hlrRequestSession.setIncomingMessageListenerList(inboundMessageListenerList);
	}

	/**
	 * Create new Session - 'SmppSessionWrapper' object
	 * @return
	 * @throws SmppClientBootstrapException
	 */
	private SmppSessionWrapper createSession() throws SmppClientBootstrapException {
		if (this.defaultClientBootstrap.get() == null) {
			try {
				this.defaultClientBootstrap.create();
			} catch (Exception e) {
				throw new SmppClientBootstrapException(e.getMessage(), e);
			}
		}

		SmppSessionWrapper newSession = new SmppSessionWrapper();
		newSession.setDefaultClientBootstrap(this.defaultClientBootstrap);
		newSession.setRegisteredDelivery(this.smppConfig.getRegisteredDelivery());

		return newSession;
	}

	/**
	 * Create 'SMS' object using the parameters mandatory to send SMS over SMPP protocol 
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user ID to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMS 
	 * @throws CreateSmsException 
	 */
	private SMS createSMS(String senderAddress, String recipientAddress, String messageText) throws CreateSmsException {				
		SMS sms = new SMS(senderAddress, recipientAddress, messageText);	

		try {		
			sms.resolveSrcTonAndNpiOptions();
			sms.resolveDestTonAndNpiOptions(recipientAddress);	
			sms.encodeUnicodeTextToBinary();
		} catch (Exception e) {
			throw new CreateSmsException(e.getMessage(), e);
		}	
		return sms;
	}
}
