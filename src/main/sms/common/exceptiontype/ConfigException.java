package sms.common.exceptiontype;

public class ConfigException extends Exception {
	static final long serialVersionUID = 1L;

	public ConfigException(String msg) {
		super(msg);
	}

	public ConfigException(String msg, Throwable t) {
		super(msg, t);
	}
}
