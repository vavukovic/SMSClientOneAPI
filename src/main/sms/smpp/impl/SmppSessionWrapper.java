package sms.smpp.impl;

import java.io.UnsupportedEncodingException;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sms.common.model.DeliveryInfoList.DeliveryInfo;
import sms.common.model.DeliveryInfoList;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.InboundSMSMessage;
import sms.common.model.InboundSMSMessageList;
import sms.common.model.SMS;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.common.response.SMSSendResponse;
import sms.smpp.config.SmppConfig;
import sms.smpp.exceptiontype.SmppEstablishConnectionException;
import sms.smpp.exceptiontype.SmppSubmitSmException;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.charset.GSMCharset;
import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.util.PduUtil;

public class SmppSessionWrapper {

	private static final Logger logger = LoggerFactory.getLogger(SmppSessionWrapper.class);
	private SmppSession session = null;
	private DefaultClientBootstrap defaultClientBootstrap = null;
	private KeepAliveGuard keepAliveGuard = null;
	private long keepAliveinterval;    
	private volatile EventListenerList deliveryReportListenerList = null;
	private volatile EventListenerList inboundMessageListenerList = null;
	private byte registeredDelivery = SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED;
	private SmppSessionConfiguration smppSessionConfig = null;
	private String systemType = ""; 
	private final String CONNECTION_EXCEPTION_MESSAGE = "Smpp connection exception occured while trying to send the message.";

	public enum DLRType {
		unknown, sms, flash, hlr;
	};

	public void setDefaultClientBootstrap(DefaultClientBootstrap defaultClientBootstrap) {
		this.defaultClientBootstrap = defaultClientBootstrap;
	}

	public void setDeliveryReportListenerList(EventListenerList listenerList) {		
		this.deliveryReportListenerList = listenerList;
	}

	public void setIncomingMessageListenerList(EventListenerList listenerList) {		
		this.inboundMessageListenerList = listenerList;	
	}

	public void setRegisteredDelivery(byte registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public boolean isInitialized() {
		return (this.session != null);
	}

	public void bind(SmppConfig smppSmsConfig) throws SmppEstablishConnectionException  {
		// create session         
		DefaultSmppSessionHandler sessionHandler = new ClientSmppSessionHandler();		 
		// create a session by having the bootstrap connect a
		// socket, send the bind request, and wait for a bind response
		this.initSmppSessionConfig(smppSmsConfig);

		try {
			this.session = this.defaultClientBootstrap.get().bind(this.smppSessionConfig, sessionHandler);
		} catch (Exception e) {
			throw new SmppEstablishConnectionException(e.getMessage(), e);
		}

		// start keep alive thread
		this.keepAliveinterval = smppSmsConfig.getKeepAliveInterval();
		this.keepAliveGuard = new KeepAliveGuard();	
		this.keepAliveGuard.Start(keepAliveinterval, session);   	
	}

	private void initSmppSessionConfig(SmppConfig smppConfig) {
		this.smppSessionConfig = new SmppSessionConfiguration();	
		this.smppSessionConfig.setWindowSize(20);
		this.smppSessionConfig.setName(smppConfig.getSmppName());
		this.smppSessionConfig.setType(SmppBindType.TRANSCEIVER);
		this.smppSessionConfig.setHost(smppConfig.getHost());
		this.smppSessionConfig.setPort(smppConfig.getPort());
		this.smppSessionConfig.setConnectTimeout(smppConfig.getConnectionTimeout());
		this.smppSessionConfig.setSystemId(smppConfig.getSystemId());
		this.smppSessionConfig.setPassword(smppConfig.getPassword());
		this.smppSessionConfig.getLoggingOptions().setLogBytes(false);
		this.smppSessionConfig.setRequestExpiryTimeout(smppConfig.getRequestExpiryTimeout());
		this.smppSessionConfig.setWindowMonitorInterval(smppConfig.getWindowMonitorInterval());
		this.smppSessionConfig.setCountersEnabled(false);
		this.smppSessionConfig.setSystemType(this.systemType);
	}

	public void unbind() {
		if (this.session != null) {
			this.deliveryReportListenerList = null;
			this.keepAliveGuard.Stop();

			if (this.session.isBound()) {
				this.session.unbind(5000);
			}

			this.session.destroy(); 	
			this.session = null;
		}
	}

	/**
	 * SubmitSm using the 'recipientAddress' parameter
	 * @param recipientAddress
	 * @return SMSSendResponse
	 * @throws SmppSubmitSmException
	 */
	public SMSSendResponse submitSm(String recipientAddress) throws SmppSubmitSmException {

		SMSSendResponse smsSendReponse = new SMSSendResponse(); 

		SubmitSm submit0 = new SubmitSm();
		submit0.setRegisteredDelivery(this.registeredDelivery);
		Address address = new Address();
		address.setAddress(recipientAddress);
		submit0.setDestAddress(address);

		try {
			SubmitSmResp submitSmResp = session.submit(submit0, 10000);

			smsSendReponse.addSMPPResponse(recipientAddress, submitSmResp.getResultMessage());
			return smsSendReponse;

		} catch (Exception e) {			
			String errorMessage = e.getMessage();		
			if (e instanceof SmppChannelException) {
				this.unbind();
				if ((errorMessage == null) || (errorMessage.isEmpty())) {
					errorMessage = CONNECTION_EXCEPTION_MESSAGE;
				}
			}	

			throw new SmppSubmitSmException(errorMessage, e);
		}	
	}

	/**
	 * SubmitSm using the 'SMS' object data
	 * @param sms
	 * @return SMSSendResponse
	 * @throws SmppSubmitSmException
	 */
	public SMSSendResponse submitSm(SMS sms) throws SmppSubmitSmException {   

		SMSSendResponse smsSendReponse = new SMSSendResponse(); 	

		try {
			if (sms.isEncodeUnicodeTextToBinary()) {
				sms.encodeUnicodeTextToBinary();
			}

			if (sms.isAutoResolveSrcTonAndNpiOptions()) {
				sms.resolveSrcTonAndNpiOptions();
			}

			byte[] textBytes = this.getBytesFromMessage(sms);

			for (String recipientAddress : sms.getRecipientsAddress()) {

				if (sms.isAutoResolveDestTonAndNpiOptions()) {
					sms.resolveDestTonAndNpiOptions(recipientAddress);
				}

				SubmitSm submit0 = new SubmitSm();
				submit0.setRegisteredDelivery(this.registeredDelivery);

				if ((sms.getSenderAddress() != null) && (!sms.getSenderAddress().isEmpty()))
					submit0.setSourceAddress(new Address((byte)sms.getSrcton(), (byte)sms.getSrcnpi(), sms.getSenderAddress()));

				if  ((recipientAddress != null) && (!recipientAddress.isEmpty()))
					submit0.setDestAddress(new Address((byte)sms.getDestton(), (byte)sms.getDestnpi(), recipientAddress));

				if ((sms.getValidityPeriod() != null) && (!sms.getValidityPeriod().isEmpty()))
					submit0.setValidityPeriod(sms.getValidityPeriod());

				if ((sms.getScheduleDeliveryTime() != null) && (!sms.getScheduleDeliveryTime().isEmpty()))
					submit0.setScheduleDeliveryTime(sms.getScheduleDeliveryTime());

				if (textBytes != null) submit0.setShortMessage(textBytes);

				submit0.setDataCoding((byte)sms.getDatacoding());
				submit0.setEsmClass((byte)sms.getEsmclass());
				submit0.setProtocolId((byte)sms.getProtocolid());

				SubmitSmResp submitSmResp = session.submit(submit0, 10000);
				smsSendReponse.addSMPPResponse(recipientAddress, submitSmResp.getResultMessage());
			}

			return smsSendReponse;	

		} catch (Exception e) {
			String errorMessage = e.getMessage();		
			if (e instanceof SmppChannelException) {
				this.unbind();
				if ((errorMessage == null) || (errorMessage.isEmpty())) {
					errorMessage = CONNECTION_EXCEPTION_MESSAGE;	
				}
			}	

			throw new SmppSubmitSmException(errorMessage, e);
		}	
	}

	/**
	 * Encode text to binary using correct 'CHARSET'
	 * @param sms
	 * @return textBytes
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getBytesFromMessage(SMS sms) throws UnsupportedEncodingException {
		byte[] textBytes = null;		
		if ((sms.getMessageBinary() != null) && (!sms.getMessageBinary().isEmpty())) {
			textBytes = HexUtil.toByteArray(sms.getMessageBinary());
		} else if ((sms.getMessageText() != null)  && (!sms.getMessageText().isEmpty())) {
			String messageText = sms.getMessageText();
			messageText = messageText.replaceAll("\r\n", "\n");
			messageText = messageText.replaceAll("\n\r", "\n");

			if (GSMCharset.canRepresent(messageText)) {
				textBytes = CharsetUtil.encode(messageText, CharsetUtil.CHARSET_GSM);
			} else {	
				textBytes = messageText.getBytes("UTF-16BE");	
			}
		}
		return textBytes;
	}

	/**
	 * Class used to handle DLR-s and INBOUND SMS messages
	 * @author vavukovic
	 *
	 */
	protected class ClientSmppSessionHandler extends DefaultSmppSessionHandler {
		protected ClientSmppSessionHandler() {
			super(logger);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void firePduRequestExpired(PduRequest pduRequest) {
			logger.warn("PDU request expired: {}", pduRequest);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public PduResponse firePduRequestReceived(PduRequest pduRequest) {
			if (PduUtil.isRequestCommandId(pduRequest.getCommandId())) {
				if (pduRequest.getCommandId() == SmppConstants.CMD_ID_DELIVER_SM) {
					this.HandleDeliverSm(pduRequest);
				}
			}

			PduResponse response = pduRequest.createResponse();
			return response;
		}	 

		@SuppressWarnings("rawtypes")
		private void HandleDeliverSm(PduRequest pduRequest) {
			DeliverSm deliverSm = (DeliverSm)pduRequest;

			if (isDeliveryReport(deliverSm.getEsmClass())) {
				this.HandleDeliveryReport(deliverSm);
			} else {
				this.HandleIncmomingMessage(deliverSm);		
			}	
		}

		/**
		 * Create 'DLR Response' using 'deliverSm' data and trigger 'DeliveryReportListener' listener 
		 * @param deliverSm
		 */
		private void HandleDeliveryReport(DeliverSm deliverSm) {	
			if (deliveryReportListenerList != null) {						
				//Create response
				SMSSendDeliveryStatusResponse response = new SMSSendDeliveryStatusResponse();
				//Create delivery info 
				DeliveryInfo deliveryInfo = new DeliveryInfo();
				deliveryInfo.setDeliveryStatus(new String(deliverSm.getShortMessage()));		
				deliveryInfo.setAddress(deliverSm.getDestAddress().getAddress());
				//Create delivery array 
				DeliveryInfo[] deliveryInfos = new DeliveryInfo[1];
				deliveryInfos[0] = deliveryInfo;
				//Create delivery info list
				DeliveryInfoList deliveryInfoList = new DeliveryInfoList();
				deliveryInfoList.setDeliveryInfo(deliveryInfos);
				response.setDeliveryInfoList(deliveryInfoList);

				Object[] listeners = deliveryReportListenerList.getListenerList();		
				// Each listener occupies two elements - the first is the listener class
				// and the second is the listener instance
				for (int i=0; i<listeners.length; i+=2) {
					if (listeners[i]==DeliveryReportListener.class) {
						((DeliveryReportListener)listeners[i+1]).onDeliveryReportReceived(response, resolveDlrType());
					}
				}	
			}		
		}

		/**
		 * Create 'INBOUND SMS Response' using 'deliverSm' data and trigger 'InboundMessageListener' listener 
		 * @param deliverSm
		 */
		private void HandleIncmomingMessage(DeliverSm deliverSm) {
			if (inboundMessageListenerList != null) {
				//Create response
				RetrieveSMSResponse response = new RetrieveSMSResponse();
				//Create INBOUND message using the DeliverSm
				InboundSMSMessage inboundMessage = new InboundSMSMessage();
				inboundMessage.setSenderAddress(deliverSm.getSourceAddress().getAddress());
				inboundMessage.setDestinationAddress(deliverSm.getDestAddress().getAddress());
				inboundMessage.setMessage(new String(deliverSm.getShortMessage()));

				//Create INBOUND messages array
				InboundSMSMessage[] inboundMessages = new InboundSMSMessage[1];
				//Add message to the array
				inboundMessages[0] = inboundMessage;
				//Create INBOUND message list
				InboundSMSMessageList inboundMessageList = new InboundSMSMessageList();
				inboundMessageList.setNumberOfMessagesInThisBatch(1);
				inboundMessageList.setTotalNumberOfPendingMessages(0);
				inboundMessageList.setInboundSMSMessage(inboundMessages);

				//set INBOUND messages list
				response.setInboundSMSMessageList(inboundMessageList);

				Object[] listeners = inboundMessageListenerList.getListenerList();
				// Each listener occupies two elements - the first is the listener class
				// and the second is the listener instance
				for (int i=0; i<listeners.length; i+=2) {
					if (listeners[i]==InboundMessageListener.class) {
						((InboundMessageListener)listeners[i+1]).onMessageRetrieved(response);
					}
				}	
			}
		}	

		/**
		 * Get session type (sms. flash, hlr)
		 * @return dlrType
		 */
		private DLRType resolveDlrType() {
			DLRType dlrType = DLRType.sms;

			if (systemType.equals(SmppSender.FLASH_NOTIFICATION_SYSTEM_TYPE)) {
				dlrType = DLRType.flash;
			} else if (systemType.equals(SmppSender.HLR_REQUEST_SYSTEM_TYPE)) {
				dlrType = DLRType.hlr;
			}

			return dlrType;
		}

		/**
		 * Check by 'esmClass' if 'deliverSm' is DLR 
		 * @param esmClass
		 * @return result
		 */
		private boolean isDeliveryReport(byte esmClass) {
			boolean result = false;
			if ((esmClass & (byte) 0x3c) > 0) {
				result = true;
			}
			return result;
		}
	}
}
