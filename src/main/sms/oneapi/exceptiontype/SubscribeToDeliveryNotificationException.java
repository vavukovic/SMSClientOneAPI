package sms.oneapi.exceptiontype;

import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;


public class SubscribeToDeliveryNotificationException extends Exception {
	static final long serialVersionUID = 1L;
	SMSDeliveryReceiptSubscriptionResponse response  = null;
	
	public SubscribeToDeliveryNotificationException(String msg) {
		super(msg);
	}

	public SubscribeToDeliveryNotificationException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SubscribeToDeliveryNotificationException(String msg, Throwable t, SMSDeliveryReceiptSubscriptionResponse response) {
		super(msg, t);
		this.response = response;
	}

	public SMSDeliveryReceiptSubscriptionResponse getResponse() {
		return response;
	}
}
