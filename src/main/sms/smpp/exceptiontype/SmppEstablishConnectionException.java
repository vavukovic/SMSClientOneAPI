package sms.smpp.exceptiontype;

public class SmppEstablishConnectionException extends Exception {
	static final long serialVersionUID = 1L;

	public SmppEstablishConnectionException(String msg) {
		super(msg);
	}

	public SmppEstablishConnectionException(String msg, Throwable t) {
		super(msg, t);
	}
}
