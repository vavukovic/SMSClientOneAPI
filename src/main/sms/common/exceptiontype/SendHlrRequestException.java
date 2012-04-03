package sms.common.exceptiontype;

public class SendHlrRequestException extends Exception {
	static final long serialVersionUID = 1L;

	public SendHlrRequestException(String msg) {
		super(msg);
	}

	public SendHlrRequestException(String msg, Throwable t) {
		super(msg, t);
	}
}
