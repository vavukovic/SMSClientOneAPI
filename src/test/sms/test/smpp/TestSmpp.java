package sms.test.smpp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sms.common.impl.SMSClient;
import sms.common.model.DeliveryReportListener;
import sms.common.model.SMS;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.common.response.SMSSendResponse;
import sms.smpp.config.SmppConfig;
import sms.smpp.impl.SmppSessionWrapper.DLRType;

public class TestSmpp {
	public static final int SERVERPORT = 9785;
	public static final String SYSTEMID = "smppclient1";
	public static final String PASSWORD = "pass";
	public static final String SENDER_ADDRESS = "somesender";
	public static final String RECIPIENT_ADDRESS = "1234567890";
	public static final String MESSAGE_TEXT = "text";
	private static SmppServerSimulator server;
	private static SMSClient client;
	private volatile SMSSendDeliveryStatusResponse dlrsPush = null;
	
	@BeforeClass
	public static void startSimulator() throws Exception {
		// init server
		server = new SmppServerSimulator();
		server.start(SERVERPORT);
	
		// init client
		client = new SMSClient(null, createSmppConfig());
	}

	@AfterClass
	public static void stopSimulator() throws Exception {
		// unbind client
		client.destroy();
		// stop server
		server.stop();
	}

	@Test
	public void testSimpleSendSMS() {
		SMSSendResponse response = null;
		
		try {
			response = client.sendSMS(SENDER_ADDRESS, RECIPIENT_ADDRESS, MESSAGE_TEXT);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(response);
	}

	@Test
	public void testSendFlashSMS() {
		SMSSendResponse response = null;
		
		// flash opens a separate smpp session
		try {
			response = client.sendFlashNotification(SENDER_ADDRESS, RECIPIENT_ADDRESS, "FLASH");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testSendHLRRequest() {
		SMSSendResponse response = null;
		
		// hlr opens a separate smpp session
		try {
			response = client.sendHLRRequest(RECIPIENT_ADDRESS);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(response);
	}

	@Test
	public void testSendMultipleRecipients() throws Exception {
		SMSSendResponse response = null;
		SMS sms = new SMS(SENDER_ADDRESS, "1111111", MESSAGE_TEXT);
		sms.addRecipientAddress("2222222");
		sms.addRecipientAddress("3333333");
		sms.addRecipientAddress("4444444");
		sms.addRecipientAddress("5555555");
		sms.addRecipientAddress("6666666");

		// send message to smpp server
		response = client.sendSMS(sms);

		Assert.assertEquals(response.getSMPPResponseList().size(), 6);
	}

	@Test
	public void testGetDLRPush() throws Exception {
		dlrsPush = null;

		// set listener
		client.addDeliveryReportListener(new DeliveryReportListener() {

			@Override
			public void onDeliveryReportReceived(SMSSendDeliveryStatusResponse response, DLRType dlrType) {
				dlrsPush = response;			
			}
		});

		// wait a little
		// after 2 seconds we expect the dlr pushed here
		Thread.sleep(2200);
		Assert.assertNotNull(dlrsPush);
		Assert.assertEquals(dlrsPush.getDeliveryInfoList().getDeliveryInfo().length, 1);
		Assert.assertEquals("id:1005 sub:001 dlvrd:001 submit date:1205232039 done date:1205242339 stat:DELIVRD err:000 text:sometext", dlrsPush.getDeliveryInfoList().getDeliveryInfo()[0].getDeliveryStatus());
	}

	private static SmppConfig createSmppConfig()  {
		SmppConfig smppConfig = new SmppConfig(SYSTEMID, PASSWORD, "localhost", SERVERPORT);	
		smppConfig.setSmppName("Tester.Session.1");
		return smppConfig;
	}

}
