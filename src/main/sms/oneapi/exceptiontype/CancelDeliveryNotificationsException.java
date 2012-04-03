package sms.oneapi.exceptiontype;

public class CancelDeliveryNotificationsException extends Exception {
	static final long serialVersionUID = 1L;

	public CancelDeliveryNotificationsException(String msg) {
		super(msg);
	}

	public CancelDeliveryNotificationsException(String msg, Throwable t) {
		super(msg, t);
	}
}
