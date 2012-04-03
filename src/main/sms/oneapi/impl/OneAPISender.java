package sms.oneapi.impl;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;

import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.RequestError;
import sms.common.exceptiontype.SendHlrRequestException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.impl.Sender;
import sms.common.impl.SMSClient.SenderType;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.SMS;
import sms.common.response.SMSSendResponse;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.model.FormParameters;
import sms.oneapi.util.JSONRequest;
import sms.oneapi.util.OneApiConnection;

public class OneAPISender implements Sender {
	
	private OneAPIConfig oneAPIConfig = null;
	private static JSONRequest<SMSSendResponse> smsSendResponseProcessor=new JSONRequest<SMSSendResponse>(new SMSSendResponse());
	private DLRStatusRetriever dlrRetriever= null;
	private InboundMessagesRetriever inboundRetriever = null;
	private volatile EventListenerList inboundMessageListenerList = null;
	private volatile EventListenerList deliveryReportListenerList = null;
	private List<String> resourceUrlList = null;
	
	//*************************OneAPISender initialization***********************************************************************************************************************************************
	/**
	 * Initialize 'OneAPISender' object
	 */
	public OneAPISender() {
		super();
	}
	
	//*************************OneAPISender public************************************************************************************************************************************************************				
	/**
	 * Initialize 'OneAPISender' object using 'oneAPIConfig'
	 * @param oneAPIConfig
	 */
	public OneAPISender(OneAPIConfig oneAPIConfig) {
		this.oneAPIConfig = oneAPIConfig;
	}

	/**
	 * Get OneAPI configuration object
	 * @return oneAPIConfig
	 */
	public OneAPIConfig getOneAPIConfig() {
		return this.oneAPIConfig;
	}
		
	/**
	 * Get resource url list used to query delivery status in the 'DLRStatusRetirever'
	 * @return resourceUrlList
	 */
	public List<String> getResourceUrlList() {
		return resourceUrlList;
	}
	
	/**
	 *  Send an SMS over OneAPI to one or more mobile terminals using the customized 'SMS' object
	 * @param sms - object containing data needed to be filled in order to send the SMS
	 * @return SMSSendResponse
	 * @throws SendSmsException
	 */
	@Override
	public SMSSendResponse sendSMS(SMS sms) throws SendSmsException {
		if (sms == null) throw new SendSmsException("'sms' parameter is null");
		
		SMSSendResponse response = new SMSSendResponse();

		FormParameters formParameters = new FormParameters();
		formParameters.put("senderAddress", sms.getSenderAddress());
		for (String addr:sms.getRecipientsAddress()) formParameters.put("address", addr);
		formParameters.put("message", sms.getMessageText());
		formParameters.put("clientCorrelator", sms.getClientCorrelator());
		formParameters.put("notifyURL", sms.getNotifyURL());
		formParameters.put("senderName", sms.getSenderName());
		formParameters.put("callbackData", sms.getCallbackData());

		int responseCode=0;
		try {				
			StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());
			buildUrl.append("/SendSMSService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));	
			buildUrl.append("/smsmessaging/outbound/");	
			buildUrl.append(URLEncoder.encode(sms.getSenderAddress(),  OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/requests");

			String url = buildUrl.toString();			
			HttpURLConnection connection =  OneApiConnection.setupConnection(url,  OneApiConnection.URL_ENCODED_CONTENT_TYPE, this.oneAPIConfig);
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

			String requestBody=JSONRequest.formEncodeParams(formParameters);
			out.write(requestBody);
			out.close();

			responseCode=connection.getResponseCode();		
			response = smsSendResponseProcessor.getResponse(connection, OneApiConnection.CREATED);
						
			System.out.println("" + responseCode);
			
			//TODO - Remove when push server will be implemented
			//Add resource url to the list which will be used to query delivery status 
			if (response.getResourceReference() != null) {
				if (resourceUrlList == null) {
					resourceUrlList = new ArrayList<String>();
				}
				
				resourceUrlList.add(response.getResourceReference().getResourceURL());
			}
				
			return response;

		} catch (Exception e) {			
			response.setHTTPResponseCode(responseCode);
			response.setContentType(OneApiConnection.URL_ENCODED_CONTENT_TYPE);		
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));
			throw new SendSmsException(e.getMessage(), e, response);
		}	     	
	}
	
	/**
	 * Send an SMS over OneAPI to one mobile terminal using mandatory parameters 
	 * @param senderAddress (mandatory) is the address to whom a responding SMS may be sent
	 * @param recipientAddress (mandatory) contains one address for end user to send to 
	 * @param messageText (mandatory) contains the message text to send
	 * @return SMSSendResponse
	 * @throws SendSmsException  
	 */
	@Override
	public SMSSendResponse sendSMS(String senderAddress, String recipientAddress, String messageText) throws SendSmsException {
		SMS sms = new SMS(senderAddress, recipientAddress, messageText);
		return this.sendSMS(sms);	
	}
		
	/**
	 * Add OneAPI 'Delivery Reports' listener
	 * @param listener - (new DeliveryReportListener) 
	 */
	@Override
	public void addDeliveryReportListener(DeliveryReportListener listener) throws DeliveryReportListenerException {		
		if (this.deliveryReportListenerList == null) {
			this.deliveryReportListenerList = new EventListenerList();
		}		
		this.deliveryReportListenerList.add(DeliveryReportListener.class, listener);	
		this.StartDLRRetriever();
	}

	/**
	 * Add OneAPI 'Inbound Messages' listener
	 * Messages are pulled automatically depending on the 'inboundMessagesRetrievingInterval' client configuration parameter
	 * @param listener - (new InboundMessageListener)
	 */
	@Override
	public void addInboundMessageListener(InboundMessageListener listener) {
		if (listener == null) return;

		if (this.inboundMessageListenerList == null) {
			this.inboundMessageListenerList = new EventListenerList();
		}

		this.inboundMessageListenerList.add(InboundMessageListener.class, listener);		
		this.StartInboundMessagesRetriever();
	}

	public EventListenerList getInboundMessageListeners() {
		return inboundMessageListenerList;
	}
	
	public EventListenerList getDeliveryReportListeners() {
		return deliveryReportListenerList;
	}

	/**
	 * Release OneAPISender resources (release listeners) 
	 */
	public void destroy() {
		this.StopDLRRetriever();
		this.StopInboundMessagesRetriever();
	}
	
	/**
	 * Get sender type
	 * @return SenderType
	 */
	@Override
	public SenderType getSenderType() {
		return SenderType.ONEAPI;
	}

	//*************************OneAPISender private************************************************************************************************************************************************************
	private void StartDLRRetriever() {
		if (this.dlrRetriever == null) {
			this.dlrRetriever = new DLRStatusRetriever();
			int intervalMs = this.oneAPIConfig.getDlrRetrievingInterval();
			this.dlrRetriever.Start(intervalMs, this);
		}		
	}

	private void StopDLRRetriever() {
		if (dlrRetriever != null) {
			dlrRetriever.Stop();
			dlrRetriever = null;
		}

		this.deliveryReportListenerList = null;
	}
	
	private void StartInboundMessagesRetriever() {
		if (this.inboundRetriever == null) {
			this.inboundRetriever = new InboundMessagesRetriever();
			int intervalMs = this.oneAPIConfig.getInboundMessagesRetrievingInterval();
			this.inboundRetriever.Start(intervalMs, this);
		}		
	}

	private void StopInboundMessagesRetriever() {
		if (inboundRetriever != null) {
			inboundRetriever.Stop();
			inboundRetriever = null;
		}

		this.inboundMessageListenerList = null;
	}
	
	//*************************NOT SUPPORTED*************************************************************************************************************************************************************	
	/**
	 * NOT SUPPORTED
	 */
	@Override
	public SMSSendResponse sendScheduledSMS(String senderAddress, String recipientAddress, String messageText, String scheduleDeliveryTime) throws NotSupportedException {	
		throw new NotSupportedException();
	}

	/**
	 * NOT SUPPORTED
	 */
	@Override
	public SMSSendResponse sendFlashNotification(String senderAddress, String recipientAddress, String messageText) throws NotSupportedException {		
		throw new NotSupportedException();
	}

	/**
	 * NOT SUPPORTED
	 */
	@Override
	public SMSSendResponse sendHLRRequest(String destination) throws SendHlrRequestException, NotSupportedException {		
		//TODO
		throw new NotSupportedException();
	}
}
