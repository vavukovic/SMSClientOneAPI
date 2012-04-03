package sms.oneapi.exceptiontype;

import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;


public class SubscribeToReceiptNotificationsException extends Exception {
	static final long serialVersionUID = 1L;
	SMSMessageReceiptSubscriptionResponse response = null;
	
	public SubscribeToReceiptNotificationsException(String msg) {
		super(msg);
	}

	public SubscribeToReceiptNotificationsException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SubscribeToReceiptNotificationsException(String msg, Throwable t, SMSMessageReceiptSubscriptionResponse response) {
		super(msg, t);
		this.response = response;
	}
}
