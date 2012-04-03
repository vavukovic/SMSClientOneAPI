package sms.common.model;

import java.util.EventListener;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.smpp.impl.SmppSessionWrapper.DLRType;

public interface DeliveryReportListener extends EventListener {
	public void onDeliveryReportReceived(SMSSendDeliveryStatusResponse response, DLRType dlrType);
}
