package sms.it.test.oneapi;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;

import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.impl.SMSClient;
import sms.common.model.SMS;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.common.response.SMSSendResponse;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.exceptiontype.CancelDeliveryNotificationsException;
import sms.oneapi.exceptiontype.CancelReceiptNotificationsException;
import sms.oneapi.exceptiontype.SubscribeToDeliveryNotificationException;
import sms.oneapi.exceptiontype.SubscribeToReceiptNotificationsException;
import sms.oneapi.response.LocationResponse;
import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;
import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;

public class TestOneAPI {
	
	public static final int SERVERPORT = 8081;
	public static final String URL = "http://localhost:8081";
	public static final String USERNAME = "simple";
	public static final String PASSWORD = "simple";
	public static final String SENDER_ADDRESS = "TestSender";
	public static final String DESTINATION_ADDRESS = "TestDestination";
	public static final String RECIPIENT_ADDRESS = "1111";
	public static final String MESSAGE_TEXT = "TestMessageText";
	public static final String CLIENT_CORRELATOR = "TestClientCorrelator";
	public static final String NOTIFY_URL = "http://TestNotifyUrl";
	public static final String SENDER_NAME = "TestSenderName";
	public static final String CALLBACK_DATA = "TestCallbackData";
	public static final String REGISTRATION_ID = "TestRegistrationId";
	public static final String REQUEST_ID = "TestRequestId";
	public static final String SUBSCRIPTION_ID = "TestSubscriptionId";
	public static final String CRITERIA = "TestCriteria";
	public static final String NOTIFICATION_FORMAT = "TestNotificationFormat";
	
	public static final String DELIVERY_INFO_REFERENCE = "{\"deliveryInfoList\":{\"deliveryInfo\": [{\"address\": \"tel:+1350000001\",\"deliveryStatus\":\"MessageWaiting\"}," +
			"{\"address\":\"tel:+1350000999\",\"deliveryStatus\":\"MessageWaiting\"}],\"resourceURL\":\"http://example.com/1/smsmessaging/outbound/tel%3A%2B12345678/requests/abc123/deliveryInfos\"}}";
	
	public static final String INBOUND_SMS_REFERENCE = "{\"inboundSMSMessageList\":{\"inboundSMSMessage\":[{\"dateTime\":\"2009-11-19T12:00:00\",\"destinationAddress\":\"3456\"" + 
			",\"messageId\":\"msg1\",\"message\": \"Come on Barca!\",\"resourceURL\":\"http://example.com/1/smsmessaging/inbound/registrations/3456/messages/msg1\", \"senderAddress\":" + 
			"\"+447825123456\"}, {\"dateTime\": \"2009-11-19T12:00:00\",\"destinationAddress\":\"3456\",\"messageId\": \"msg2\",\"message\": \"Great goal by Messi\",\"resourceURL\":" + 
			"\"http://example.com/1/smsmessaging/inbound/registrations/3456/messages/msg2\",\"senderAddress\": \"+447825789123\"}], \"numberOfMessagesInThisBatch\": \"2\", \"resourceURL\":" +
			"\"http://example.com/1/smsmessaging/inbound/registrations/3456/messages\",\"totalNumberOfPendingMessages\": \"20\"}}";
	
	private static final String LOCATE_TERMINAL_REFERENCE = "{\"terminalLocationList\": {\"terminalLocation\":[{\"address\":\"tel:16309700001\",\"locationRetrievalStatus\":\"Retrieved\"," +
			"\"currentLocation\":{\"accuracy\":100.0,\"altitude\":1001.0,\"latitude\":-80.86302,\"longitude\":41.277306,\"timestamp\":\"2012-04-02T13:12:57Z\"},\"errorInformation\":null}]}}";
	
	private static SMSClient client = null;
	private static OneAPIServerSimulator server = null;

    @BeforeClass
    public static void startSimulator() throws Exception {
    	// initialize http server
    	server = new OneAPIServerSimulator(SERVERPORT);
		new Thread(server).start();
		
		// initialize client using OneAPIConfig
		client = new SMSClient(createOneAPIConfig());		
    }

    @AfterClass
    public static void stopSimulator() {
    	// kill oneAPI server
    	server.release();
    	server = null;
    	client = null;
    }
    
    @Test
    public void testSendSMSUsingSMSObject() {
    	SMS sms = composeSms();
    	SMSSendResponse response = null;
		try {
			response = client.sendSMS(sms);
		} catch (SendSmsException e) {
			Assert.fail("Error occured while trying to send the message. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);
    	// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted message parameters with the reference parameters
    	Assert.assertEquals("senderAddress=TestSender&address=1111&message=TestMessageText&clientCorrelator=TestClientCorrelator&notifyURL=http%3A%2F%2FTestNotifyUrl&senderName=TestSenderName&callbackData=TestCallbackData", server.getPostRequest());
    }
    
    @Test
    public void testSendMultipleSMSUsingSMSObject() {
    	SMS sms = composeSms();
    	sms.addRecipientAddress("2222");
    	sms.addRecipientAddress("3333");
    	sms.addRecipientAddress("4444");
    	sms.addRecipientAddress("5555");
    	
    	SMSSendResponse response = null;
		try {
			response = client.sendSMS(sms);
		} catch (SendSmsException e) {
			Assert.fail("Error occured while trying to send the message. Err: " + e.getMessage());
		}
		
		Assert.assertNotNull(response);   	
		// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted message parameters with the reference parameters
    	Assert.assertEquals("senderAddress=TestSender&address=1111&address=2222&address=3333&address=4444&address=5555&message=TestMessageText&clientCorrelator=TestClientCorrelator&notifyURL=http%3A%2F%2FTestNotifyUrl&senderName=TestSenderName&callbackData=TestCallbackData", server.getPostRequest());
    }
    
    @Test
    public void testSendSMSError() {
    	SMS sms = null;
    	SMSSendResponse response = null;
    	
		try {
			response = client.sendSMS(sms);
		} catch (SendSmsException e) {
			Assert.assertNotNull(e);
		}
		
		Assert.assertNull(response);
    }
       
    @Test
    public void testSendSimpleSMS() {
    	SMSSendResponse response = null;
    	
		try {
			response = client.sendSMS(SENDER_ADDRESS, RECIPIENT_ADDRESS, MESSAGE_TEXT);
		} catch (Exception e) {
			Assert.fail("Error occured while trying to send the message. Err: " + e.getMessage());
		}
    	
		Assert.assertNotNull(response);
    	// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted message parameters with the reference parameters
    	Assert.assertEquals("senderAddress=TestSender&address=1111&message=TestMessageText&clientCorrelator=&notifyURL=&senderName=&callbackData=", server.getPostRequest());
    }
       
    @Test
    public void testQueryDeliveryStatus() { 	
    	server.setResponse(DELIVERY_INFO_REFERENCE);
    	
    	SMSSendDeliveryStatusResponse refResponse = new SMSSendDeliveryStatusResponse();
		ObjectMapper mapper = new ObjectMapper();
		try {
			refResponse = mapper.readValue(DELIVERY_INFO_REFERENCE, SMSSendDeliveryStatusResponse.class);
		} catch (Exception e) {
			Assert.fail("Error occured while trying to query delivery status. Err: " + e.getMessage());
		}
			
    	SMSSendDeliveryStatusResponse response = null;
		try {
			response = client.queryDeliveryStatus(SENDER_ADDRESS, REQUEST_ID);
		} catch (QueryDeliveryStatusException e) {
			Assert.fail("Error occured while trying to query delivery status. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);
    	 	
    	// 200 – Success
    	Assert.assertEquals(200, response.getHTTPResponseCode());  	
    	
    	// check if delivery info exists
    	Assert.assertNotNull(response.getDeliveryInfoList());
    	Assert.assertNotNull(response.getDeliveryInfoList().getDeliveryInfo()[0]);
    	
    	// compare response parameters with the reference parameters
    	Assert.assertEquals(refResponse.getDeliveryInfoList().toString(), response.getDeliveryInfoList().toString());  	   
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getResourceURL(), response.getDeliveryInfoList().getResourceURL());  	   
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getDeliveryInfo()[0].getAddress(), response.getDeliveryInfoList().getDeliveryInfo()[0].getAddress());  	
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getDeliveryInfo()[0].getDeliveryStatus(), response.getDeliveryInfoList().getDeliveryInfo()[0].getDeliveryStatus());  	
    }
    
    @Test
    public void testQueryDeliveryStatusUsingUrlParam() { 	
    	server.setResponse(DELIVERY_INFO_REFERENCE);
    	
    	SMSSendDeliveryStatusResponse refResponse = new SMSSendDeliveryStatusResponse();
		ObjectMapper mapper = new ObjectMapper();
		try {
			refResponse = mapper.readValue(DELIVERY_INFO_REFERENCE, SMSSendDeliveryStatusResponse.class);
		} catch (Exception e) {
			Assert.fail("Error occured while trying to query delivery status. Err: " + e.getMessage());
		}
			
    	String url = "http://localhost:8081/outbound/tel%3A%2B12345678/requests/abc123/deliveryInfos";
    	SMSSendDeliveryStatusResponse response = null;
		try {
			response = client.queryDeliveryStatusByUrl(url);
		} catch (QueryDeliveryStatusException e) {
			Assert.fail("Error occured while trying to query delivery status. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);
    	 	
    	//200 = Success
    	Assert.assertEquals(200, response.getHTTPResponseCode());  	
    	
    	// check if delivery info exists
    	Assert.assertNotNull(response.getDeliveryInfoList());
    	Assert.assertNotNull(response.getDeliveryInfoList().getDeliveryInfo()[0]);
    	
    	// compare response parameters with the reference parameters
    	Assert.assertEquals(refResponse.getDeliveryInfoList().toString(), response.getDeliveryInfoList().toString());  	   
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getResourceURL(), response.getDeliveryInfoList().getResourceURL());  	   
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getDeliveryInfo()[0].getAddress(), response.getDeliveryInfoList().getDeliveryInfo()[0].getAddress());  	
    	Assert.assertEquals(refResponse.getDeliveryInfoList().getDeliveryInfo()[0].getDeliveryStatus(), response.getDeliveryInfoList().getDeliveryInfo()[0].getDeliveryStatus());  	
    }
    
    @Test
    public void subscribeToDeliveryNotificationsWithoutOptionalParams() { 	
    	SMSDeliveryReceiptSubscriptionResponse response = null;
		try {
			response = client.subscribeToDeliveryNotifications(SENDER_ADDRESS, NOTIFY_URL);
		} catch (SubscribeToDeliveryNotificationException e) {
			Assert.fail("Error occured while trying to subcribe to delivery notifications. Err: " + e.getMessage());
		}   
		
    	Assert.assertNotNull(response);   	
		// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted subscription parameters with the reference parameters
    	Assert.assertEquals("clientCorrelator=&notifyURL=http%3A%2F%2FTestNotifyUrl&callbackData=", server.getPostRequest()); 	
    }
    
    @Test
    public void subscribeToDeliveryNotificationsWithOptionalParams() { 	
    	SMSDeliveryReceiptSubscriptionResponse response = null;
		try {
			response = client.subscribeToDeliveryNotifications(SENDER_ADDRESS, NOTIFY_URL, CRITERIA, CLIENT_CORRELATOR, CALLBACK_DATA);
		} catch (SubscribeToDeliveryNotificationException e) {
			Assert.fail("Error occured while trying to subcribe to delivery notifications. Err: " + e.getMessage());
		}   
		
    	Assert.assertNotNull(response);   	
    	// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted subscription parameters with the reference parameters
    	Assert.assertEquals("clientCorrelator=TestClientCorrelator&notifyURL=http%3A%2F%2FTestNotifyUrl&callbackData=TestCallbackData", server.getPostRequest()); 	
    }
    
    @Test
    public void cancelDeliveryNotifications() { 	
    	int response = 0;
		try {
			response = client.cancelDeliveryNotifications(SUBSCRIPTION_ID);
		} catch (CancelDeliveryNotificationsException e) {
			Assert.fail("Error occured while trying to cancel delivery notifications. Err: " + e.getMessage());
		}
		Assert.assertNotNull(response);   	
		//204 - indicates the subscription has been deleted (No content)
    	Assert.assertEquals(204, response);  	
    }
    
    @Test
    public void retrieveInboundMessages() { 	
    	server.setResponse(INBOUND_SMS_REFERENCE);
    	
    	RetrieveSMSResponse refResponse = new RetrieveSMSResponse();
		RetrieveSMSResponse response = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			refResponse = mapper.readValue(INBOUND_SMS_REFERENCE, RetrieveSMSResponse.class);
			response = client.retrieveInboundMessages(REGISTRATION_ID, 100);
		} catch (Exception e) {
			Assert.fail("Error occured while trying to retrieve inbound messages. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);
    	 	
    	//200 = Success
    	Assert.assertEquals(200, response.getHTTPResponseCode());  	
    	
    	// check if inbound message exists
    	Assert.assertNotNull(response.getInboundSMSMessageList());
    	Assert.assertNotNull(response.getInboundSMSMessageList().getInboundSMSMessage()[0]);
    	
    	// compare response parameters with the reference parameters
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().toString(), response.getInboundSMSMessageList().toString());  	   
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getResourceURL(), response.getInboundSMSMessageList().getResourceURL());  
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getSenderAddress(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getSenderAddress());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getDestinationAddress(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getDestinationAddress());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessage(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessage());  	
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessageId(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessageId());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getDateTime(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getDateTime());
    }
    
    @Test
    public void retrieveInboundMessagesUsingConfigParams() { 	
    	server.setResponse(INBOUND_SMS_REFERENCE);
    	
    	RetrieveSMSResponse refResponse = new RetrieveSMSResponse();
    	RetrieveSMSResponse response = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			refResponse = mapper.readValue(INBOUND_SMS_REFERENCE, RetrieveSMSResponse.class);	
			response = client.retrieveInboundMessages();
			
		} catch (Exception e) {
			Assert.fail("Error occured while trying to retrieve inbound messages. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);
    	 	
    	//200 = Success
    	Assert.assertEquals(200, response.getHTTPResponseCode());  	
    	
    	// check if inbound message exists
    	Assert.assertNotNull(response.getInboundSMSMessageList());
    	Assert.assertNotNull(response.getInboundSMSMessageList().getInboundSMSMessage()[0]);
    	
    	// compare response parameters with the reference parameters
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().toString(), response.getInboundSMSMessageList().toString());  	   
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getResourceURL(), response.getInboundSMSMessageList().getResourceURL());  
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getSenderAddress(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getSenderAddress());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getDestinationAddress(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getDestinationAddress());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessage(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessage());  	
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessageId(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getMessageId());
    	Assert.assertEquals(refResponse.getInboundSMSMessageList().getInboundSMSMessage()[0].getDateTime(), response.getInboundSMSMessageList().getInboundSMSMessage()[0].getDateTime());
    }
    
    @Test
    public void subscribeToReceiptNotificationsWithoutOptionalParams() { 	
    	SMSMessageReceiptSubscriptionResponse response = null;
    	
		try {
			response = client.subscribeToReceiptNotifications(DESTINATION_ADDRESS, NOTIFY_URL);
		} catch (SubscribeToReceiptNotificationsException e) {
			Assert.fail("Error occured while trying to subcribe to receipt notifications. Err: " + e.getMessage());
		}
		
    	Assert.assertNotNull(response);   	
    	// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted subscription parameters with the reference parameters
    	Assert.assertEquals("destinationAddress=TestDestination&notifyURL=http%3A%2F%2FTestNotifyUrl&criteria=&notificationFormat=&clientCorrelator=&callbackData=", server.getPostRequest()); 	
    }
    
    @Test
    public void subscribeToReceiptNotificationsWithOptionalParams() { 	
    	SMSMessageReceiptSubscriptionResponse response = null;
		try {
			response = client.subscribeToReceiptNotifications(DESTINATION_ADDRESS, NOTIFY_URL, CRITERIA, NOTIFICATION_FORMAT, CLIENT_CORRELATOR, CALLBACK_DATA);
		} catch (SubscribeToReceiptNotificationsException e) {
			Assert.fail("Error occured while trying to subcribe to receipt notifications. Err: " + e.getMessage());
		}   	
		
    	Assert.assertNotNull(response);   	
    	// 201 - Created. The message resource was created and is being queued for delivery.
    	Assert.assertEquals(201, response.getHTTPResponseCode());  	
    	// compare posted subscription parameters with the reference parameters
    	Assert.assertEquals("destinationAddress=TestDestination&notifyURL=http%3A%2F%2FTestNotifyUrl&criteria=TestCriteria&notificationFormat=TestNotificationFormat&clientCorrelator=TestClientCorrelator&callbackData=TestCallbackData", server.getPostRequest()); 	
    }
      
    @Test
    public void cancelReceiptNotifications() { 	
    	int response = 0;	
		try {
			response = client.cancelReceiptNotifications(SUBSCRIPTION_ID);
		} catch (CancelReceiptNotificationsException e) {
			Assert.fail("Error occured while trying to receipt notificationss. Err: " + e.getMessage());
		}	
		Assert.assertNotNull(response);   	
		//204 - indicates the subscription has been deleted (No content)
    	Assert.assertEquals(204, response);  	
    }
    
    @Test
    public void locateTerminal() {
    	server.setResponse(LOCATE_TERMINAL_REFERENCE);

    	LocationResponse refResponse = new LocationResponse();
    	LocationResponse response = null;
    	ObjectMapper mapper = new ObjectMapper();

    	try {

    		String[] addresses = new String[1];
    		addresses[0] = DESTINATION_ADDRESS;

    		refResponse = mapper.readValue(LOCATE_TERMINAL_REFERENCE, LocationResponse.class);
    		response = client.locateMultipleTerminals(addresses, 20);

    		Assert.assertNotNull(response);

    		//200 = Success
    		Assert.assertEquals(200, response.getHTTPResponseCode());  	
    		
    		// check if mobile terminal location exists
        	Assert.assertNotNull(response.getTerminalLocationList());
        	Assert.assertNotNull(response.getTerminalLocationList().getTerminalLocation()[0]);
        	
        	// compare response parameters with the reference parameters
        	Assert.assertEquals(refResponse.getTerminalLocationList().toString(), response.getTerminalLocationList().toString());  	   
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getAddress(), response.getTerminalLocationList().getTerminalLocation()[0].getAddress());  
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getLocationRetrievalStatus(), response.getTerminalLocationList().getTerminalLocation()[0].getLocationRetrievalStatus());  	
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getErrorInformation(), response.getTerminalLocationList().getTerminalLocation()[0].getErrorInformation());    	
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getErrorInformation(), response.getTerminalLocationList().getTerminalLocation()[0].getErrorInformation());  	
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getTimestamp(), response.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getTimestamp());  
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getAccuracy(), response.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getAccuracy());  
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getAltitude(), response.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getAltitude());  
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getLatitude(), response.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getLatitude());  
        	Assert.assertEquals(refResponse.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getLongitude(), response.getTerminalLocationList().getTerminalLocation()[0].getCurrentLocation().getLongitude());  
        		
    	} catch (Exception e) {
    		Assert.fail("Error occured while trying to locate specified mobile terminal. Err: " + e.getMessage());
    	}
    }
       
    private static OneAPIConfig createOneAPIConfig()  {
		OneAPIConfig config = new OneAPIConfig(URL, USERNAME, PASSWORD);	
		return config;
	}
   
    private static SMS composeSms() {
    	SMS sms = null;

    	sms = new SMS(SENDER_ADDRESS, RECIPIENT_ADDRESS, MESSAGE_TEXT);
    	sms.setClientCorrelator(CLIENT_CORRELATOR);
    	sms.setNotifyURL(NOTIFY_URL);
    	sms.setSenderName(SENDER_NAME);
    	sms.setCallbackData(CALLBACK_DATA);

    	Assert.assertNotNull(sms);  

    	return sms;
    }
}
