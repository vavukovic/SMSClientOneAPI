package sms.oneapi.exceptiontype;

import sms.common.response.RetrieveSMSResponse;

public class RetrieveInboundMessagesException extends Exception {
	static final long serialVersionUID = 1L;
	RetrieveSMSResponse response = null;
	
	public RetrieveInboundMessagesException(String msg) {
		super(msg);
	}

	public RetrieveInboundMessagesException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RetrieveInboundMessagesException(String msg, Throwable t, RetrieveSMSResponse response) {
		super(msg, t);
		this.response = response;
	}

	public RetrieveSMSResponse getResponse() {
		return response;
	}
}
