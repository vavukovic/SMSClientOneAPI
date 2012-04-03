package sms.common.exceptiontype;

import sms.common.response.SMSSendResponse;

public class SendSmsException extends Exception {
	static final long serialVersionUID = 1L;
	SMSSendResponse response = null;
	
	public SendSmsException(String msg) {
		super(msg);
	}

	public SendSmsException(String msg, Throwable t) {
		super(msg, t);
	}	
	
	public SendSmsException(String msg, Throwable t, SMSSendResponse response) {
		super(msg, t);
		this.response = response;
	}

	public SMSSendResponse getResponse() {
		return response;
	}
}
