package sms.smpp.exceptiontype;

public class SmppClientBootstrapException extends Exception {
	static final long serialVersionUID = 1L;

	public SmppClientBootstrapException(String msg) {
		super(msg);
	}

	public SmppClientBootstrapException(String msg, Throwable t) {
		super(msg, t);
	}
}
