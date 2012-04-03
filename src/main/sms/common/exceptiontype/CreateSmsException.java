package sms.common.exceptiontype;

public class CreateSmsException extends Exception {
	static final long serialVersionUID = 1L;

	public CreateSmsException(String msg) {
		super(msg);
	}

	public CreateSmsException(String msg, Throwable t) {
		super(msg, t);
	}
}
