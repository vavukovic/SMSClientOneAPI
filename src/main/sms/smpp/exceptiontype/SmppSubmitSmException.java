package sms.smpp.exceptiontype;

public class SmppSubmitSmException extends Exception {
	static final long serialVersionUID = 1L;

	public SmppSubmitSmException(String msg) {
		super(msg);
	}

	public SmppSubmitSmException(String msg, Throwable t) {	
		super(msg, t);	
	}
}
