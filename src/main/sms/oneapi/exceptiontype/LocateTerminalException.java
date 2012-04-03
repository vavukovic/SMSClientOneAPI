package sms.oneapi.exceptiontype;

import sms.oneapi.response.LocationResponse;

public class LocateTerminalException extends Exception {
	static final long serialVersionUID = 1L;
	LocationResponse response = null;
	
	public LocateTerminalException(String msg) {
		super(msg);
	}

	public LocateTerminalException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public LocateTerminalException(String msg, Throwable t, LocationResponse response) {
		super(msg, t);
		this.response = response;
	}
}
