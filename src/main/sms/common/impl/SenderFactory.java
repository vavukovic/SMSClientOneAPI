package sms.common.impl;

import sms.common.impl.SMSClient.SenderType;
import sms.oneapi.config.OneAPIConfig;
import sms.oneapi.impl.OneAPISender;
import sms.smpp.config.SmppConfig;
import sms.smpp.impl.SmppSender;

public class SenderFactory {
	
	/**
	 * Create 'OneAPI' sender
	 * @param oneApiConfig
	 * @return
	 */
	public Sender createOneAPISender(OneAPIConfig oneApiConfig)
	{
		return this.CreateSender(SenderType.ONEAPI, oneApiConfig, null);
	}
	
	/**
	 * Create 'SMPP' sender
	 * @param smppConfig
	 * @return
	 */
	public Sender createSMPPSender(SmppConfig smppConfig)
	{
		return this.CreateSender(SenderType.SMPP, null, smppConfig);
	}
	
	/**
	 * Create sender depending on the "sender" parameter (SenderType.ONEAPI, SenderType.SMPP)
	 * @param senderType
	 * @param oneApiConfig
	 * @param smppConfig
	 * @return
	 */
	public Sender CreateSender(SenderType senderType, OneAPIConfig oneApiConfig, SmppConfig smppConfig)
	{
		if (senderType.equals(SenderType.SMPP)) {
			return new SmppSender(smppConfig);
		}
		else 
		{ 
			return new OneAPISender(oneApiConfig);
		}
	}
}