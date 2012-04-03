package sms.oneapi.exceptiontype;

public class CancelReceiptNotificationsException extends Exception {
	static final long serialVersionUID = 1L;

	public CancelReceiptNotificationsException(String msg) {
		super(msg);
	}

	public CancelReceiptNotificationsException(String msg, Throwable t) {
		super(msg, t);
	}
}
