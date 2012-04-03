package sms.oneapi.impl;

import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.oneapi.exceptiontype.CancelDeliveryNotificationsException;
import sms.oneapi.exceptiontype.CancelReceiptNotificationsException;
import sms.oneapi.exceptiontype.LocateTerminalException;
import sms.oneapi.exceptiontype.RetrieveInboundMessagesException;
import sms.oneapi.exceptiontype.SubscribeToDeliveryNotificationException;
import sms.oneapi.exceptiontype.SubscribeToReceiptNotificationsException;
import sms.oneapi.response.LocationResponse;
import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;
import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;

public interface OneAPI {
	/**
	 * Locate a single specified mobile terminal to the specified level of accuracy
	 * @param address (mandatory) The MSISDN or Anonymous Customer Reference of the mobile device to locate. The protocol and Ô+Ő identifier must be used for MSISDN. Do not URL escape prior to passing to the locateTerminal function as this will be done by the API
	 * @param requestedAccuracy (mandatory) The preferred accuracy of the result, in metres. Typically, when you request an accurate location it will take longer to retrieve than a coarse location. So requestedAccuracy=10 will take longer than requestedAccuracy=100
	 * @return LocationResponse
	 * @throws LocateTerminalException 
	 * @throws NotSupportedException 
	 */
	public LocationResponse locateTerminal(String address, int requestedAccuracy) throws LocateTerminalException, NotSupportedException;
	
	/**
	 * Locate multiple specified mobile terminals to the specified level of accuracy
	 * @param addresses (mandatory) The MSISDN or Anonymous Customer Reference of the mobile device to locate. The protocol and Ô+Ő identifier must be used for MSISDN. Do not URL escape prior to passing to the locateMultipleTerminals function as this will be done by the API. Note that if any element of the address array is null it will not be sent to the OneAPI server.
	 * @param requestedAccuracy (mandatory) The preferred accuracy of the result, in metres. Typically, when you request an accurate location it will take longer to retrieve than a coarse location. So requestedAccuracy=10 will take longer than requestedAccuracy=100
	 * @return LocationResponse
	 * @throws LocateTerminalException 
	 * @throws NotSupportedException 
	 */
	public LocationResponse locateMultipleTerminals(String[] addresses, int requestedAccuracy) throws LocateTerminalException, NotSupportedException;	
	
	/**
	 * Query the delivery status for an SMS sent to one or more mobile terminals                        
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param requestId (mandatory) contains the requestId returned from a previous call to the sendSMS function 
	 * @return SMSSendDeliveryStatusResponse
	 * @throws NotSupportedException 
	 * @throws QueryDeliveryStatusException 
	 */
	public SMSSendDeliveryStatusResponse queryDeliveryStatus(String senderAddress, String requestId) throws NotSupportedException, QueryDeliveryStatusException;

	/**
	 * Query the delivery status for an SMS sent to one or more mobile terminals                         
	 * @param resourceUrl (mandatory) - url to query the delivery status. For convenience this URI is also included in the 'SMSSendResponse' response body as the resourceURL pair within the resourceReference object. 
	 * @return SMSSendDeliveryStatusResponse
	 * @throws NotSupportedException 
	 * @throws QueryDeliveryStatusException 
	 */
	public SMSSendDeliveryStatusResponse queryDeliveryStatusByUrl(String resourceUrl) throws NotSupportedException, QueryDeliveryStatusException;
	
	/**
	 * Start subscribing to delivery status notifications for all your sent SMS  	                          
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of delivery sent
	 * @return SMSDeliveryReceiptSubscriptionResponse
	 * @throws NotSupportedException 
	 * @throws SubscribeToDeliveryNotificationException 
	 */
	public SMSDeliveryReceiptSubscriptionResponse subscribeToDeliveryNotifications(String senderAddress,  String notifyURL) throws NotSupportedException, SubscribeToDeliveryNotificationException;

	/**
	 * Start subscribing to delivery status notifications for all your sent SMS 	                          
	 * @param senderAddress (mandatory) is the address from which SMS messages are being sent. Do not URL encode this value prior to passing to this function
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of delivery sent
	 * @param criteria (optional) Text in the message to help you route the notification to a specific application. For example you may ask users to ‘text GIGPICS to 12345′ for your rock concert photos application. This text is matched against the first word, as defined as the initial characters after discarding any leading Whitespace and ending with a Whitespace or end of the string. The matching shall be case-insensitive.
	 * @param clientCorrelator (optional) uniquely identifies this subscription request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid setting up the same subscription twice
	 * @param callbackData (optional) will be passed back to the notifyURL location, so you can use it to identify the message the delivery receipt relates to (or any other useful data, such as a function name)
	 * @return SMSDeliveryReceiptSubscriptionResponse
	 * @throws NotSupportedException 
	 * @throws SubscribeToDeliveryNotificationException 
	 */
	public SMSDeliveryReceiptSubscriptionResponse subscribeToDeliveryNotifications(String senderAddress, String notifyURL, String criteria, String clientCorrelator, String callbackData) throws NotSupportedException, SubscribeToDeliveryNotificationException ;

	/**
	 * Stop subscribing to delivery status notifications for all your sent SMS  
	 * @param subscriptionId (mandatory) contains the subscriptionId of a previously created SMS delivery receipt subscription
	 * return responseCode (integer)
	 * @throws CancelDeliveryNotificationsException 
	 * @throws NotSupportedException 
	 */
	public int cancelDeliveryNotifications(String subscriptionId) throws CancelDeliveryNotificationsException, NotSupportedException;

	/**
	 * Stop subscribing to delivery status notifications over OneAPI for all your sent SMS                         
	 * @param resourceUrl (mandatory) - url to stop subscribing to delivery status notifications. For convenience this URI is also included in the 'SMSDeliveryReceiptSubscriptionResponse' response body as the resourceURL pair within the deliveryReceiptSubscription object. 
	 * @return responseCode (integer)
	 * @throws CancelDeliveryNotificationsException 
	 */
	public int cancelDeliveryNotificationsByUrl(String resourceUrl) throws CancelDeliveryNotificationsException;
	
	/**
	 * Retrieve SMS messages sent to your Web application 
	 * @param registrationId (mandatory) - loaded from the client 'OneAPIConfig' object
	 * @return RetrieveSMSResponse
	 * @throws NotSupportedException 
	 * @throws RetrieveInboundMessagesException 
	 */
	public RetrieveSMSResponse retrieveInboundMessages() throws NotSupportedException, RetrieveInboundMessagesException;

	/**
	 * Retrieve SMS messages sent to your Web application over OneAPI
	 * @param registrationId (mandatory) is agreed with your network operator for receiving messages
	 * @return RetrieveSMSResponse
	 * @throws RetrieveInboundMessagesException 
	 */
	public RetrieveSMSResponse retrieveInboundMessages(String registrationId) throws RetrieveInboundMessagesException;
	
	/**
	 * Retrieve SMS messages sent to your Web application 
	 * @param registrationId (mandatory) is agreed with your network operator for receiving messages
	 * @param  maxBatchSize (mandatory) is the maximum number of messages to retrieve in this request
	 * @return RetrieveSMSResponse
	 * @throws NotSupportedException 
	 * @throws RetrieveInboundMessagesException 
	 */
	public RetrieveSMSResponse retrieveInboundMessages(String registrationId, int maxBatchSize) throws NotSupportedException, RetrieveInboundMessagesException;

	/**
	 * Start subscribing to notifications of SMS messages sent to your application                         
	 * @param destinationAddress (mandatory) is the address/ MSISDN, or code agreed with the operator, to which people may send an SMS to your application
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of message receipts sent
	 * @return SMSMessageReceiptSubscriptionResponse
	 * @throws NotSupportedException 
	 * @throws SubscribeToReceiptNotificationsException 
	 */
	public SMSMessageReceiptSubscriptionResponse subscribeToReceiptNotifications(String destinationAddress, String notifyURL) throws NotSupportedException, SubscribeToReceiptNotificationsException;

	/**
	 * Start subscribing to notifications of SMS messages sent to your application                       
	 * @param destinationAddress (mandatory) is the address/ MSISDN, or code agreed with the operator, to which people may send an SMS to your application
	 * @param notifyURL (mandatory) is the URL to which you would like a notification of message receipts sent
	 * @param criteria (optional) is case-insensitve text to match against the first word of the message, ignoring any leading whitespace. This allows you to reuse a short code among various applications, each of which can register their own subscription with different criteria
	 * @param notificationFormat (optional) is the content type that notifications will be sent in Đ for OneAPI v1.0 only JSON is supported
	 * @param clientCorrelator (optional) uniquely identifies this create subscription request. If there is a communication failure during the request, using the same clientCorrelator when retrying the request allows the operator to avoid creating a duplicate subscription
	 * @param callbackData (optional) is a function name or other data that you would like included when the POST is sent to your application
	 * @return SMSMessageReceiptSubscriptionResponse
	 * @throws NotSupportedException 
	 * @throws SubscribeToReceiptNotificationsException 
	 */
	public SMSMessageReceiptSubscriptionResponse subscribeToReceiptNotifications(String destinationAddress, String notifyURL, String criteria, String notificationFormat, String clientCorrelator, String callbackData) throws NotSupportedException, SubscribeToReceiptNotificationsException;

	/**
	 * Stop subscribing to message receipt notifications for all your received SMS                       
	 * @param subscriptionId (mandatory) contains the subscriptionId of a previously created SMS message receipt subscription
	 * @return responseCode (integer)
	 * @throws CancelReceiptNotificationsException 
	 * @throws NotSupportedException 
	 */
	public int cancelReceiptNotifications(String subscriptionId) throws CancelReceiptNotificationsException, NotSupportedException;

	/**
	 * Stop subscribing to message receipt notifications for all your received SMS over OneAPI                     
	 * @param resourceUrl (mandatory) - url to stop subscribing to message receipt notifications. For convenience this URI is also included in the 'SMSMessageReceiptSubscriptionResponse' response body as the resourceURL pair within the resourceReference object. 
	 * @return responseCode (integer)
	 * @throws CancelReceiptNotificationsException 
	 */
	public int cancelReceiptNotificationsByUrl(String resourceUrl) throws CancelReceiptNotificationsException;
}
