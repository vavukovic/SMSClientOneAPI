package sms.common.exceptiontype;

public class NotSupportedException extends Exception {
	static final long serialVersionUID = 1L;

	public NotSupportedException(String msg) {
		super(msg);
	}

	public NotSupportedException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public NotSupportedException() {
		super("Not supported for the configured 'Sender Type'.");
	}
}
