package sms.smpp.response;

public class SMPPResponse {
	
	private String recipientAddress = "";
	private String response = "";
	
	public SMPPResponse(String recipientAddress, String response) {
		this.recipientAddress = recipientAddress;
		this.response = response;
	}

	public String getRecipientAddress() {
		return recipientAddress;
	}
	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}	
	
	public String toString() {
		return "address = ".concat(recipientAddress) + ",  responseStatus = ".concat(response);
	}
}
