package sms.it.test.smpp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.infobip.smscsim.ServerSimulatorListenerAdapter;
import org.infobip.smscsim.Simulator;
import org.infobip.smscsim.SmartSimulator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smpp.pdu.SubmitSMResp;
import org.infobip.smscsim.SMSCSession;
import org.smpp.pdu.IBExtension;
import org.smpp.pdu.SubmitSM;
import org.smpp.util.PDUUtils;
import org.smpp.pdu.Request;
import org.smpp.pdu.Response;
import sms.common.impl.SMSClient;
import sms.common.model.InboundMessageListener;
import sms.common.model.InboundSMSMessage;
import sms.common.model.SMS;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendResponse;
import sms.smpp.config.SmppConfig;

public class TestSmpp {

	private static ServerSimulatorListenerAdapter smppSimulatorListener;
	private static Collection<GatewayMessage> receivedGatewayMessages;
	private static SmartSimulator smartSimulator;
	private static SMSClient client;
	public static final int SERVERPORT = 9785;
	public static final String SYSTEMID = "smppclient1";
	public static final String PASSWORD = "pass";
	public static final String SENDER_ERROR = "Sender is not equal.";
	public static final String RECIPIENT_ERROR = "Recipient is not equal.";
	public static final String TEXT_ERROR = "Text is not equal.";
			
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		smartSimulator = SmppInfobipServerSimulator.create(SERVERPORT);
		
		receivedGatewayMessages = new ConcurrentLinkedQueue<GatewayMessage>();
		prepareDefaultGatewaySubmitSmppSimulatorListener();
		
		Simulator simulator = smartSimulator.getSimulator();
		simulator.addListener(smppSimulatorListener);
	
		// init client
		client = new SMSClient(null, createSmppConfig());
	
		// add listener
		client.addInboundMessageListener(new InboundMessageListener() {	
			
			@Override
			public void onMessageRetrieved(RetrieveSMSResponse retrievingResponse) {
				Assert.assertTrue(retrievingResponse != null);
				Assert.assertTrue(retrievingResponse.getInboundSMSMessageList() != null);
				Assert.assertTrue(retrievingResponse.getInboundSMSMessageList().getInboundSMSMessage() != null);
				Assert.assertTrue(retrievingResponse.getInboundSMSMessageList().getInboundSMSMessage().length == 1);
				
				InboundSMSMessage inboundMessage = retrievingResponse.getInboundSMSMessageList().getInboundSMSMessage()[0];
								
				Assert.assertEquals(SENDER_ERROR, "testSender", inboundMessage.getSenderAddress());
				Assert.assertEquals(RECIPIENT_ERROR, "555", inboundMessage.getDestinationAddress());
				Assert.assertEquals(TEXT_ERROR, "testText", inboundMessage.getMessage());
				
			}
		});
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.destroy();
		smartSimulator.stopSimulator();	
	}
	
	@Test
	public void testSimpleSendSMS() {
		SMSSendResponse response = null;
		
		try {
			response = client.sendSMS("SIMPLESENDER", "111", "SIMPLETEXT");
			Assert.assertNotNull(response);
			
			Assert.assertNotNull(response.getSMPPResponseList());
			Assert.assertNotNull(response.getSMPPResponseList().get(0));
			
			Assert.assertEquals(response.getSMPPResponseList().get(0).getRecipientAddress(), "111");
			Assert.assertEquals(response.getSMPPResponseList().get(0).getResponse(), "OK");
			
			GatewayMessage[] x = (GatewayMessage[]) receivedGatewayMessages.toArray(new GatewayMessage[receivedGatewayMessages.size()]);		
			Assert.assertEquals(SENDER_ERROR, "SIMPLESENDER", x[receivedGatewayMessages.size() - 1].getSender());	
			Assert.assertEquals(RECIPIENT_ERROR, "111", x[receivedGatewayMessages.size() - 1].getRecipient());	
			Assert.assertEquals(TEXT_ERROR, "SIMPLETEXT", (new String(x[receivedGatewayMessages.size() - 1].getBinaryMessageContent())));
			
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
			response = client.sendFlashNotification("FLASHSENDER", "444", "FLASHTEXT");
			Assert.assertNotNull(response);
			
			Assert.assertNotNull(response.getSMPPResponseList());
			Assert.assertNotNull(response.getSMPPResponseList().get(0));
			
			Assert.assertEquals(response.getSMPPResponseList().get(0).getRecipientAddress(), "444");
			Assert.assertEquals(response.getSMPPResponseList().get(0).getResponse(), "OK");
			
			GatewayMessage[] x = (GatewayMessage[]) receivedGatewayMessages.toArray(new GatewayMessage[receivedGatewayMessages.size()]);		
			Assert.assertEquals(SENDER_ERROR, "FLASHSENDER", x[receivedGatewayMessages.size() - 1].getSender());	
			Assert.assertEquals(RECIPIENT_ERROR, "444", x[receivedGatewayMessages.size() - 1].getRecipient());	
			Assert.assertEquals(TEXT_ERROR, "FLASHTEXT", (new String(x[receivedGatewayMessages.size() - 1].getBinaryMessageContent())));
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(response);
	}
		
//	@Test
//	public void testSendHLRRequest() {
//		SMSSendResponse response = null;
//		
//		// hlr opens a separate smpp session
//		try {
//			response = client.sendHLRRequest("1234567890");
//		} catch (Exception e) {
//			Assert.fail(e.getMessage());
//		}
//		Assert.assertNotNull(response);
//	}

	@Test
	public void testSendMultipleRecipients() throws Exception {
		SMSSendResponse response = null;
		SMS sms = new SMS("SENDER", "1111111", "MULTIPLE");
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
	public void testIncomingMessage() {
		String response = null;
		
		// flash opens a separate smpp session
		try {
			response = sendRequest("http://localhost:9786/DeliverSM/?systemId=".concat(SYSTEMID).concat("&from=testSender&to=555&message=testText"));	
				
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(response);
	}
	
	private String sendRequest(String uriSpec) throws Exception{
		URL url = new URL(uriSpec);
		URLConnection conn = url.openConnection();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String line;

		while ((line = br.readLine()) != null)
		{
			sb.append(line);
		}
		
		br.close();

	    return sb.toString();		  
	}
	
	/**
	 * prepares the default SMPP listener<br/>
	 * which collects received gateway messages/traffic
	 */
	private static void prepareDefaultGatewaySubmitSmppSimulatorListener() throws Exception {
		smppSimulatorListener = new ServerSimulatorListenerAdapter() {
			@Override
			public void onClientRequest(SMSCSession session, Request request) {
				if (!(request instanceof SubmitSM)) {
					return;
				}
				SubmitSM submitSM = (SubmitSM) request;
				byte[] content = submitSM.getShortMessageData().getBuffer();
				GatewayMessage message = new GatewayMessage("", content, submitSM.getSourceAddr().getAddress(), submitSM.getDestAddr().getAddress(), null, submitSM.getSourceAddr().getTon(), submitSM.getSourceAddr().getNpi(), submitSM.getDestAddr().getTon(), submitSM.getDestAddr().getNpi());
				@SuppressWarnings("deprecation")
				IBExtension extension = PDUUtils.getIBExtension(request);
				message.setServingHlr(extension.getServingHLR());
				message.setServingMsc(extension.getServingMSC());
				message.setImsi(extension.getIMSI());
				receivedGatewayMessages.add(message);
			}

			@Override
			public void onServerResponse(SMSCSession session, Response response) {
				if (!(response instanceof SubmitSMResp)) {
					return;
				}
				SubmitSMResp submitSMResp = (SubmitSMResp) response;
				Assert.assertTrue(submitSMResp.getValid() == (byte)3);
			}
		};
	}
	
	private static SmppConfig createSmppConfig()  {
		SmppConfig smppConfig = new SmppConfig(SYSTEMID, PASSWORD, "localhost", SERVERPORT);	
		smppConfig.setSmppName("Tester.Session.1");
		return smppConfig;
	}
}
