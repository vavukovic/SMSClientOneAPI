package sms.common.exceptiontype;

public class DeliveryReportListenerException extends Exception {
	static final long serialVersionUID = 1L;

	public DeliveryReportListenerException(String msg) {
		super(msg);
	}

	public DeliveryReportListenerException(String msg, Throwable t) {
		super(msg, t);
	}
}
