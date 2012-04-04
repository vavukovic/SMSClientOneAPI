package sms.common.impl;

import sms.common.config.MainConfig;
import sms.common.exceptiontype.ConfigException;
import sms.common.exceptiontype.CreateSmsException;
import sms.common.exceptiontype.NotSupportedException;
import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.exceptiontype.SendHlrRequestException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.model.SMS;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.common.response.SMSSendResponse;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.exceptiontype.CancelDeliveryNotificationsException;
import sms.oneapi.exceptiontype.CancelReceiptNotificationsException;
import sms.oneapi.exceptiontype.LocateTerminalException;
import sms.oneapi.exceptiontype.RetrieveInboundMessagesException;
import sms.oneapi.exceptiontype.SubscribeToDeliveryNotificationException;
import sms.oneapi.exceptiontype.SubscribeToReceiptNotificationsException;
import sms.oneapi.model.Authorization;
import sms.oneapi.response.LocationResponse;
import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;
import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;
import sms.smpp.config.SmppConfig;

public class Main {

	@SuppressWarnings("unused")
	public static void main(String[] args) {	
				
		//1.)	Initialize SMS Client with empty constructor
		//-	configuration data are loaded from the „client.cfg“ file
		//-	“sender“  - configuration property determines if the messages will be sent using „OneAPI“ or „SMPP“ (SenderType.ONEAPI, SenderType.SMPP)  
		//-	other functionalities are always available using  the „OneAPI“(retrieveInboundMessages, subcribeToDeliveryNotifications, subcribeToReceiptNotifications...)

		//Initialize client
		SMSClient client1 = null;
		try {
			client1 = new SMSClient();
		} catch (ConfigException e1) {
			e1.printStackTrace();
		} 

		
		//2.)	Initialize SMS Client using „OneAPIConfig“ as constructor parameter
		//-	messages are sent using „OneAPI“
		//-	other functionalities are always available using  the „OneAPI“(retrieveInboundMessages, subcribeToDeliveryNotifications, subcribeToReceiptNotifications...)
		
		//Create OneAPI configuration object
		OneAPIConfig oneAPIConfig = new OneAPIConfig("http://localhost:8080/infobip-oneapi/rest/SendSMSService/1/smsmessaging", "SmppBasicSend", "SmppBasic");	
		//Initialize client
		try {
			SMSClient client2 = new SMSClient(oneAPIConfig);
		} catch (ConfigException e1) {
			e1.printStackTrace();
		}  

		
		//3.) Initialize SMS Client using „OneAPIConfig“, „SmppConfig“ as constructor parameters
		//-	messages are sent using „SMPP“
		//-	other functionalities are always available using  the „OneAPI“(retrieveInboundMessages, subcribeToDeliveryNotifications, subcribeToReceiptNotifications...)

		//Create OneAPI configuration object
		OneAPIConfig oneAPIConfig1 = new OneAPIConfig("http://localhost:8080/infobip-oneapi/rest/SendSMSService/1/smsmessaging", "SmppBasicSend", "SmppBasic");	
		//Create SMPP configuration object
		SmppConfig smppConfig = new SmppConfig("SmppBasicSend", "SmppBasic", "127.0.0.1", 9000);
		//Initialize client
		try {
			SMSClient client3 = new SMSClient(oneAPIConfig1, smppConfig);
		} catch (ConfigException e1) {
			e1.printStackTrace();
		}    

		
		//4.) Create new SMS object	
		SMS sms1 = new SMS ();
		sms1.setSenderAddress("senderAddress");
		sms1.addRecipientAddress("3859578576576");
		sms1.setMessageText("messageText");
		sms1.setClientCorrelator("clientCorrelator");
		sms1.setNotifyURL("http://notifyURL");
		sms1.setSenderName("senderName");
		sms1.setCallbackData("callbackData");

		//..or for SMPP with different parameters	
		SMS sms2 = new SMS("SENDER", "385563657436", "TEXT");
		sms2.setProtocolid(1);
		sms2.setMessageBinary("AE432");
		sms2.setAutoResolveDestTonAndNpiOptions(true);
		sms2.setAutoResolveSrcTonAndNpiOptions(true);
		sms2.setEncodeUnicodeTextToBinary(true);
	
		
		//5.) SEND SMS		
		//Send SMS using created object 
		try {
			SMSSendResponse response = client1.sendSMS(sms1);
		} catch (SendSmsException e1) {
			e1.printStackTrace();
		}
			
		//------  Or send using alternative methods.. ------

		//Send SMS using mandatory parameters 	
		try {
			SMSSendResponse response = client1.sendSMS("senderAddress", "385563657436", "messageText");
		} catch (CreateSmsException e1) {
			e1.printStackTrace();
		} catch (SendSmsException e1) {
			e1.printStackTrace();
		}
		
		//Send Scheduled SMS using mandatory parameters
		try {
			SMSSendResponse response1 = client1.sendScheduledSMS("senderAddress", "385563657436", "messageText", "YYMMDDhhmmsstnnp");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Send Flash Notification SMS using mandatory parameters
		try {
			SMSSendResponse response2 = client1.sendFlashNotification("senderAddress", "385563657436", "messageText");
		} catch (CreateSmsException e) {
			e.printStackTrace();
		} catch (SendSmsException e) {
			e.printStackTrace();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		}
		
		//Send HLR request 
		try {
			SMSSendResponse response3 = client1.sendHLRRequest("385957391837");
		} catch (SendHlrRequestException e) {
			e.printStackTrace();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		}

		//6.) Other OneAPI methods
		//Query delivery status 
		try {
			SMSSendDeliveryStatusResponse response4 = client1.queryDeliveryStatus("senderAddress", "requestId");
		} catch (QueryDeliveryStatusException e) {
			e.printStackTrace();
		}
			
		//Query delivery status using resource URL that can be found in the „sent message“ response  
		try {
			SMSSendDeliveryStatusResponse response5 = client1.queryDeliveryStatusByUrl("http://resourceUrl");
		} catch (QueryDeliveryStatusException e) {
			e.printStackTrace();
		}

		//Retrieve INBOUND messages
		try {
			RetrieveSMSResponse response6 = client1.retrieveInboundMessages("registrationId", 100);
		} catch (RetrieveInboundMessagesException e) {
			e.printStackTrace();
		}

		//Retrieve INBOUND messages using parameters from the oneAPI configuration (registrationId)
		try {
			RetrieveSMSResponse response7 = client1.retrieveInboundMessages();
		} catch (RetrieveInboundMessagesException e) {
			e.printStackTrace();
		}
				
		//Subscribe to delivery notifications
		try {
			SMSDeliveryReceiptSubscriptionResponse response8 = client1.subscribeToDeliveryNotifications("senderAddress", "notifyURL");
		} catch (SubscribeToDeliveryNotificationException e) {
			e.printStackTrace();
		}
				
		//Cancel delivery notifications
		try {
			int response9 = client1.cancelDeliveryNotifications("subscriptionId");
		} catch (CancelDeliveryNotificationsException e) {
			e.printStackTrace();
		}
			
		//Subscribe to receipt notifications
		try {
			SMSMessageReceiptSubscriptionResponse response10 = client1.subscribeToReceiptNotifications ("385563657436", "notifyURL");
		} catch (SubscribeToReceiptNotificationsException e) {
			e.printStackTrace();
		}
				
		//Cancel receipt notifications
		try {
			int response11 = client1.cancelReceiptNotifications("subscriptionId");
		} catch (CancelReceiptNotificationsException e) {
			e.printStackTrace();
		}
		
		//locate a single specified mobile terminal to the specified level of accuracy
		try {
			LocationResponse response12 = client1.locateTerminal("3859529475928938", 20);
		} catch (LocateTerminalException e) {
			e.printStackTrace();
		}

		//Example create main configuration object
		MainConfig config = new MainConfig();	
		//Set OneAPI configuration values
		config.getOneAPI().setSmsMessagingRootUrl("http://www.test.com");
		config.getOneAPI().setVersionOneAPISMS("http://www.test.com");
		config.getOneAPI().setAuthorization(new Authorization("TestUserName", "TestPassword"));		
		
		//Example to save configuration to configuration file (e.g. client.cfg) 
//		try {
//			config.saveToConfigFile();  -- Note!!!: it will overwrite "client.cfg" file
//		} catch (ConfigException e) {
//			e.printStackTrace();
//		}
		
		//Example to load configuration from configuration file (e.g. client.cfg) 
		try {
			config.loadFromConfigFile();
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		
		//Print SMS messaging URL to console
		System.out.println(config.getOneAPI().getSmsMessagingRootUrl());
		System.out.println(config.getOneAPI().getVersionOneAPISMS());
		System.out.println(config.getOneAPI().getAuthorization().getUsername());
		System.out.println(config.getOneAPI().getAuthorization().getPassword());
			
		
		//Check if specified text is UNICODE
		System.out.println("Is 'čćwwwww' UNICODE?" +  String.valueOf(client1.isUnicode("čćwwwww")));	
		//Check if specified text is UNICODE
		System.out.println("Is 'ertfdgd' UNICODE?" +  String.valueOf(client1.isUnicode("ertfdgd")));		
	}	
}
