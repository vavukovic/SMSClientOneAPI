package sms.common.exceptiontype;

import sms.common.response.SMSSendDeliveryStatusResponse;

public class QueryDeliveryStatusException extends Exception {
	static final long serialVersionUID = 1L;
	SMSSendDeliveryStatusResponse response = null;
	
	public QueryDeliveryStatusException(String msg) {
		super(msg);
	}

	public QueryDeliveryStatusException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public QueryDeliveryStatusException(String msg, Throwable t, SMSSendDeliveryStatusResponse response) {
		super(msg, t);
		this.response = response;
	}

	public SMSSendDeliveryStatusResponse getResponse() {
		return response;
	}
}
