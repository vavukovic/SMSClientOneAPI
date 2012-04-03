package sms.oneapi.impl;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.exceptiontype.RequestError;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.exceptiontype.CancelDeliveryNotificationsException;
import sms.oneapi.exceptiontype.CancelReceiptNotificationsException;
import sms.oneapi.exceptiontype.LocateTerminalException;
import sms.oneapi.exceptiontype.RetrieveInboundMessagesException;
import sms.oneapi.exceptiontype.SubscribeToDeliveryNotificationException;
import sms.oneapi.exceptiontype.SubscribeToReceiptNotificationsException;
import sms.oneapi.model.FormParameters;
import sms.oneapi.response.LocationResponse;
import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;
import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;
import sms.oneapi.util.JSONRequest;
import sms.oneapi.util.OneApiConnection;

public class OneAPIImpl implements OneAPI {
	protected OneAPIConfig oneAPIConfig = null;
	private static JSONRequest<SMSSendDeliveryStatusResponse> smsSendDeliveryStatusProcessor=new JSONRequest<SMSSendDeliveryStatusResponse>(new SMSSendDeliveryStatusResponse());
	private static JSONRequest<SMSDeliveryReceiptSubscriptionResponse> smsDeliveryReceiptSubscriptionProcessor=new JSONRequest<SMSDeliveryReceiptSubscriptionResponse>(new SMSDeliveryReceiptSubscriptionResponse());
	private static JSONRequest<RetrieveSMSResponse> retrieveSMSprocessor=new JSONRequest<RetrieveSMSResponse>(new RetrieveSMSResponse());
	private static JSONRequest<SMSMessageReceiptSubscriptionResponse> smsMessageReceiptSubscriptionProcessor=new JSONRequest<SMSMessageReceiptSubscriptionResponse>(new SMSMessageReceiptSubscriptionResponse());
	private static JSONRequest<LocationResponse> locationRequester=new JSONRequest<LocationResponse>(new LocationResponse());
	
	//*************************OneAPIImpl initialization***********************************************************************************************************************************************
	/**
	 * Initialize 'OneAPIImpl' object 
	 */
	public OneAPIImpl() {
		super();
	}

	/**
	 * Initialize OneAPIImpl object using 'oneAPIConfig'
	 * @param oneAPIConfig
	 */
	public OneAPIImpl(OneAPIConfig oneAPIConfig) {
		this.oneAPIConfig = oneAPIConfig;
	}
	
	//*************************OneAPIImpl public******************************************************************************************************************************************************
	/**
	 * Locate a single specified mobile terminal to the specified level of accuracy
	 * @param address (mandatory) The MSISDN or Anonymous Customer Reference of the mobile device to locate. The protocol and Ô+Ő identifier must be used for MSISDN. Do not URL escape prior to passing to the locateTerminal function as this will be done by the API
	 * @param requestedAccuracy (mandatory) The preferred accuracy of the result, in metres. Typically, when you request an accurate location it will take longer to retrieve than a coarse location. So requestedAccuracy=10 will take longer than requestedAccuracy=100
	 * @return LocationResponse
	 * @throws LocateTerminalException 
	 */
	@Override
	public LocationResponse locateTerminal(String address, int requestedAccuracy) throws LocateTerminalException {
		String[] addresses = new String[1];
		addresses[0] = address;

		return this.locateMultipleTerminals(addresses, requestedAccuracy);
	}
	
	/**
	 * Locate multiple specified mobile terminals to the specified level of accuracy
	 * @param addresses (mandatory) The MSISDN or Anonymous Customer Reference of the mobile device to locate. The protocol and Ô+Ő identifier must be used for MSISDN. Do not URL escape prior to passing to the locateMultipleTerminals function as this will be done by the API. Note that if any element of the address array is null it will not be sent to the OneAPI server.
	 * @param requestedAccuracy (mandatory) The preferred accuracy of the result, in metres. Typically, when you request an accurate location it will take longer to retrieve than a coarse location. So requestedAccuracy=10 will take longer than requestedAccuracy=100
	 * @return LocationResponse
	 * @throws LocateTerminalException 
	 */
	@Override
	public LocationResponse locateMultipleTerminals(String[] addresses, int requestedAccuracy) throws LocateTerminalException {
		if (addresses == null) throw new LocateTerminalException("'addresses' parameter is null");
		
		LocationResponse response=new LocationResponse();
			
		int responseCode=0;
		String contentType = null;

		try {
			
			StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());
			buildUrl.append("/LocationService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));	
			buildUrl.append("/location/queries/location?requestedAccuracy=");	
			buildUrl.append(URLEncoder.encode(String.valueOf(requestedAccuracy), OneApiConnection.CHAR_ENCODING));	
			
			for (String address:addresses) {
				if (address != null) { 
					buildUrl.append("&address=");	
					buildUrl.append(URLEncoder.encode(address, OneApiConnection.CHAR_ENCODING));
				}
			}
			
			String url = buildUrl.toString();
			HttpURLConnection connection =  OneApiConnection.setupConnection(url, this.oneAPIConfig);		
			responseCode=connection.getResponseCode();
			contentType = connection.getContentType();

			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);
			response=locationRequester.getResponse(connection,  OneApiConnection.OK); 		
			return response;
			
		} catch (Exception e) {
			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));
			throw new LocateTerminalException(e.getMessage(), e, response);
		}
	}
	
	/**
	 * Query the delivery status over OneAPI for an SMS sent to one or more mobile terminals                       
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param requestId (mandatory) contains the requestId returned from a previous call to the sendSMS function 
	 * @return SMSSendDeliveryStatusResponse
	 * @throws QueryDeliveryStatusException
	 */
	@Override
	public SMSSendDeliveryStatusResponse queryDeliveryStatus(String senderAddress, String requestId) throws QueryDeliveryStatusException {
		SMSSendDeliveryStatusResponse response = new SMSSendDeliveryStatusResponse();
		StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());	
		
		try {					
			buildUrl.append("/QuerySMSService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/smsmessaging/outbound/");		
			buildUrl.append(URLEncoder.encode(senderAddress, OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/requests/");
			buildUrl.append(URLEncoder.encode(requestId,  OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/deliveryInfos");
			
		} catch (Exception e) {			
			throw new QueryDeliveryStatusException(e.getMessage(), e, response);	
		}	
		
		return this.queryDeliveryStatusByUrl(buildUrl.toString());
	}
	
	/**
	 * Query the delivery status over OneAPI for an SMS sent to one or more mobile terminals                         
	 * @param resourceUrl (mandatory) - url to query the delivery status. For convenience this URI is also included in the 'SMSSendResponse' response body as the resourceURL pair within the resourceReference object. 
	 * @return SMSSendDeliveryStatusResponse
	 * @throws QueryDeliveryStatusException 
	 */
	@Override
	public SMSSendDeliveryStatusResponse queryDeliveryStatusByUrl(String resourceUrl) throws QueryDeliveryStatusException {
		SMSSendDeliveryStatusResponse response = new SMSSendDeliveryStatusResponse();

		int responseCode=0;
		String contentType = null; 

		try {		
			String url = resourceUrl.toString();
			if (!url.endsWith("/deliveryInfos")) {
				url = url.concat("/deliveryInfos");
			}

			HttpURLConnection connection = OneApiConnection.setupConnection(url, this.oneAPIConfig);
			responseCode = connection.getResponseCode();		
			contentType = connection.getContentType();

			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);
			response=smsSendDeliveryStatusProcessor.getResponse(connection, OneApiConnection.OK);
			return response;

		} catch (Exception e) {
			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);		
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));		
			throw new QueryDeliveryStatusException(e.getMessage(), e, response);		
		}	
	}

	/**
	 * Start subscribing to delivery status notifications over OneAPI for all your sent SMS 	                          
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of delivery sent
	 * @return SMSDeliveryReceiptSubscriptionResponse
	 * @throws SubscribeToDeliveryNotificationException 
	 */
	@Override
	public SMSDeliveryReceiptSubscriptionResponse subscribeToDeliveryNotifications(String senderAddress,  String notifyURL) throws SubscribeToDeliveryNotificationException {
		return this.subscribeToDeliveryNotifications(senderAddress, notifyURL, "", "", "");
	}

	/**
	 * Start subscribing to delivery status notifications over OneAPI for all your sent SMS  	                          
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of delivery sent
	 * @param criteria (optional) Text in the message to help you route the notification to a specific application. For example you may ask users to ‘text GIGPICS to 12345′ for your rock concert photos application. This text is matched against the first word, as defined as the initial characters after discarding any leading Whitespace and ending with a Whitespace or end of the string. The matching shall be case-insensitive.
	 * @param clientCorrelator (optional) uniquely identifies this subscription request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid setting up the same subscription twice
	 * @param callbackData (optional) will be passed back to the notifyURL location, so you can use it to identify the message the delivery receipt relates to (or any other useful data, such as a function name)
	 * @return SMSDeliveryReceiptSubscriptionResponse
	 * @throws SubscribeToDeliveryNotificationException 
	 */
	@Override
	public SMSDeliveryReceiptSubscriptionResponse subscribeToDeliveryNotifications(String senderAddress, String notifyURL, String criteria, String clientCorrelator, String callbackData) throws SubscribeToDeliveryNotificationException {
		SMSDeliveryReceiptSubscriptionResponse response = new SMSDeliveryReceiptSubscriptionResponse();

		FormParameters formParameters=new FormParameters();
		formParameters.put("clientCorrelator", clientCorrelator);
		formParameters.put("notifyURL", notifyURL);
		formParameters.put("callbackData", callbackData);

		int responseCode=0;
		try {
			StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());
			buildUrl.append("/SMSDeliveryService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/smsmessaging/outbound/");
			buildUrl.append(URLEncoder.encode(senderAddress, OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/subscriptions");
			
			String url = buildUrl.toString();	
			HttpURLConnection connection =  OneApiConnection.setupConnection(url,  OneApiConnection.URL_ENCODED_CONTENT_TYPE, this.oneAPIConfig);
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

			String requestBody=JSONRequest.formEncodeParams(formParameters);
			out.write(requestBody);
			out.close();

			responseCode=connection.getResponseCode();		
			response=smsDeliveryReceiptSubscriptionProcessor.getResponse(connection,  OneApiConnection.CREATED);
			return response;

		} catch (Exception e) {
			response.setHTTPResponseCode(responseCode);
			response.setContentType(OneApiConnection.URL_ENCODED_CONTENT_TYPE);
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));
			throw new SubscribeToDeliveryNotificationException(e.getMessage(), e, response);
		}
	}

	/**
	 * Stop subscribing to delivery status notifications over OneAPI for all your sent SMS 
	 * @param subscriptionId (mandatory) contains the subscriptionId of a previously created SMS delivery receipt subscription
	 * return responseCode (integer)
	 * @throws CancelDeliveryNotificationsException  
	 */
	@Override
	public int cancelDeliveryNotifications(String subscriptionId) throws CancelDeliveryNotificationsException {	
		StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());

		try {		
			buildUrl.append("/CancelSMSDeliveryService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/outbound/subscriptions/");
			buildUrl.append(URLEncoder.encode(subscriptionId,  OneApiConnection.CHAR_ENCODING));
			
		} catch (Exception e) {		
			throw new CancelDeliveryNotificationsException(e.getMessage(), e);
		}	

		return this.cancelDeliveryNotificationsByUrl(buildUrl.toString());
	}
	
	/**
	 * Stop subscribing to delivery status notifications over OneAPI for all your sent SMS                         
	 * @param resourceUrl (mandatory) - url to stop subscribing to delivery status notifications. For convenience this URI is also included in the 'SMSDeliveryReceiptSubscriptionResponse' response body as the resourceURL pair within the deliveryReceiptSubscription object. 
	 * @return responseCode (integer)
	 * @throws CancelDeliveryNotificationsException 
	 */
	@Override
	public int cancelDeliveryNotificationsByUrl(String resourceUrl) throws CancelDeliveryNotificationsException {
		int responseCode=0;		
		try {
			HttpURLConnection connection =  OneApiConnection.setupConnection(resourceUrl, this.oneAPIConfig);					
			connection.setRequestMethod("DELETE");

			responseCode=connection.getResponseCode();
			return responseCode;

		} catch (Exception e) {		
			throw new CancelDeliveryNotificationsException(e.getMessage(), e);
		}	
	}
	
	/**
	 * Retrieve SMS messages sent to your Web application over OneAPI
	 * @param registrationId (mandatory) - loaded from the client 'OneAPIConfig' object
	 * @return RetrieveSMSResponse
	 * @throws RetrieveInboundMessagesException  
	 */
	@Override
	public RetrieveSMSResponse retrieveInboundMessages() throws RetrieveInboundMessagesException {
		return this.retrieveInboundMessages(this.oneAPIConfig.getRetrieveInboundMessagesRegistrationId(), 0);	
	}
	
	/**
	 * Retrieve SMS messages sent to your Web application over OneAPI
	 * @param registrationId (mandatory) is agreed with your network operator for receiving messages
	 * @return RetrieveSMSResponse
	 * @throws RetrieveInboundMessagesException 
	 */
	@Override
	public RetrieveSMSResponse retrieveInboundMessages(String registrationId) throws RetrieveInboundMessagesException {
		return this.retrieveInboundMessages(registrationId, 0);
	}
	
	/**
	 * Retrieve SMS messages sent to your Web application over OneAPI
	 * @param registrationId (mandatory) is agreed with your network operator for receiving messages
	 * @param  maxBatchSize (optional) is the maximum number of messages to retrieve in this request
	 * @return RetrieveSMSResponse
	 * @throws RetrieveInboundMessagesException 
	 */
	@Override
	public RetrieveSMSResponse retrieveInboundMessages(String registrationId, int maxBatchSize) throws RetrieveInboundMessagesException {
		RetrieveSMSResponse response=new RetrieveSMSResponse();

		int responseCode=0;
		String contentType = null;
		try {		
			StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());
			buildUrl.append("/RetrieveSMSService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));	
			buildUrl.append("/smsmessaging/inbound/registrations/");	
			buildUrl.append(URLEncoder.encode(registrationId,  OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/messages");
				
			if (maxBatchSize > 0) {
				buildUrl.append("?maxBatchSize=");
				buildUrl.append(URLEncoder.encode(String.valueOf(maxBatchSize),  OneApiConnection.CHAR_ENCODING));
			}
			
			String url = buildUrl.toString();
			HttpURLConnection connection =  OneApiConnection.setupConnection(url, this.oneAPIConfig);		
			responseCode=connection.getResponseCode();
			contentType = connection.getContentType();

			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);
			response=retrieveSMSprocessor.getResponse(connection,  OneApiConnection.OK);
			return response;	

		} catch (Exception e) {
			response.setHTTPResponseCode(responseCode);
			response.setContentType(contentType);
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));		
			throw new RetrieveInboundMessagesException(e.getMessage(), e, response);                                                  
		}	
	}

	/**
	 * Start subscribing to notifications of SMS messages sent to your application over OneAPI                          
	 * @param destinationAddress (mandatory) is the address/ MSISDN, or code agreed with the operator, to which people may send an SMS to your application
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of message receipts sent
	 * @return SMSMessageReceiptSubscriptionResponse
	 * @throws SubscribeToReceiptNotificationsException  
	 */
	@Override
	public SMSMessageReceiptSubscriptionResponse subscribeToReceiptNotifications(String destinationAddress, String notifyURL) throws SubscribeToReceiptNotificationsException {
		return this.subscribeToReceiptNotifications(destinationAddress, notifyURL, "", "", "", "");
	}

	/**
	 * Start subscribing to notifications of SMS messages sent to your application over OneAPI                           
	 * @param destinationAddress (mandatory) is the address/ MSISDN, or code agreed with the operator, to which people may send an SMS to your application
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of message receipts sent
	 * @param criteria (optional) is case-insensitve text to match against the first word of the message, ignoring any leading whitespace. This allows you to reuse a short code among various applications, each of which can register their own subscription with different criteria
	 * @param notificationFormat (optional) is the content type that notifications will be sent in Đ for OneAPI v1.0 only JSON is supported
	 * @param clientCorrelator (optional) uniquely identifies this create subscription request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid creating a duplicate subscription
	 * @param callbackData (optional) is a function name or other data that you would like included when the POST is sent to your application
	 * @return SMSMessageReceiptSubscriptionResponse
	 * @throws SubscribeToReceiptNotificationsException  
	 */
	@Override
	public SMSMessageReceiptSubscriptionResponse subscribeToReceiptNotifications(String destinationAddress, String notifyURL, String criteria, String notificationFormat, String clientCorrelator, String callbackData) throws SubscribeToReceiptNotificationsException {
		SMSMessageReceiptSubscriptionResponse response = new SMSMessageReceiptSubscriptionResponse();

		FormParameters formParameters=new FormParameters();
		formParameters.put("destinationAddress", destinationAddress);
		formParameters.put("notifyURL", notifyURL);
		formParameters.put("criteria", criteria);
		formParameters.put("notificationFormat", notificationFormat);
		formParameters.put("clientCorrelator", clientCorrelator);
		formParameters.put("callbackData", callbackData);

		int responseCode=0;
		try {
			StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());		
			buildUrl.append("/SMSReceiptService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));	
			buildUrl.append("/smsmessaging/inbound/subscriptions");
			
			String url = buildUrl.toString();
			HttpURLConnection connection =  OneApiConnection.setupConnection(url,  OneApiConnection.URL_ENCODED_CONTENT_TYPE, this.oneAPIConfig);		
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

			String requestBody=JSONRequest.formEncodeParams(formParameters);
			out.write(requestBody);
			out.close();

			responseCode=connection.getResponseCode();
			response=smsMessageReceiptSubscriptionProcessor.getResponse(connection,  OneApiConnection.CREATED);
			return response;

		} catch (Exception e) {
			response.setHTTPResponseCode(responseCode);
			response.setContentType( OneApiConnection.URL_ENCODED_CONTENT_TYPE);
			response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));			
			throw new SubscribeToReceiptNotificationsException(e.getMessage(), e, response);
		}

	}

	/**
	 * Stop subscribing to message receipt notifications for all your received SMS over OneAPI                           
	 * @param subscriptionId (mandatory) contains the subscriptionId of a previously created SMS message receipt subscription
	 * @return responseCode (integer)
	 * @throws CancelReceiptNotificationsException  
	 */
	@Override
	public int cancelReceiptNotifications(String subscriptionId) throws CancelReceiptNotificationsException {	
		StringBuilder buildUrl = new StringBuilder(this.oneAPIConfig.getSmsMessagingRootUrl());
		
		try {		
			buildUrl.append("/CancelSMSReceiptService/");
			buildUrl.append(URLEncoder.encode(this.oneAPIConfig.getVersionOneAPISMS(), OneApiConnection.CHAR_ENCODING));
			buildUrl.append("/inbound/subscriptions/");
			buildUrl.append(URLEncoder.encode(subscriptionId, OneApiConnection.CHAR_ENCODING));
		
		} catch (Exception e) {
			throw new CancelReceiptNotificationsException(e.getMessage(), e);
		}	
		
		return this.cancelReceiptNotificationsByUrl(buildUrl.toString());
	}
	
	/**
	 * Stop subscribing to message receipt notifications for all your received SMS over OneAPI                     
	 * @param resourceUrl (mandatory) - url to stop subscribing to message receipt notifications. For convenience this URI is also included in the 'SMSMessageReceiptSubscriptionResponse' response body as the resourceURL pair within the resourceReference object. 
	 * @return responseCode (integer)
	 * @throws CancelReceiptNotificationsException 
	 */
	@Override
	public int cancelReceiptNotificationsByUrl(String resourceUrl) throws CancelReceiptNotificationsException {
		int responseCode=0;		
		try {
			HttpURLConnection connection =  OneApiConnection.setupConnection(resourceUrl, this.oneAPIConfig);					
			connection.setRequestMethod("DELETE");

			responseCode=connection.getResponseCode();
			return responseCode;

		} catch (Exception e) {		
			throw new CancelReceiptNotificationsException(e.getMessage(), e);
		}	
	}
}
