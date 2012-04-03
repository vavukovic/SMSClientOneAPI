package sms.common.exceptiontype;

public class InboundMessageListenerException extends Exception {
	static final long serialVersionUID = 1L;

	public InboundMessageListenerException(String msg) {
		super(msg);
	}

	public InboundMessageListenerException(String msg, Throwable t) {
		super(msg, t);
	}
}
