package sms.common.response;

import java.util.ArrayList;
import java.util.List;
import sms.common.exceptiontype.RequestError;
import sms.common.model.ResourceReference;
import sms.smpp.response.SMPPResponse;

/**
 * The full response from the OneAPI server for a request to send an SMS Messgage
 */
public class SMSSendResponse implements java.io.Serializable {
	private static final long serialVersionUID = 1746683143328583536L;

	/**
	 * contains the HTTP response code returned from the server
	 */
	int httpResponseCode=0;
	/** 
	 * contains the HTTP Content-Type returned from the server if available
	 */
	String contentType=null;
	/**
	 * in the case the server has returned an error contains the error response.
	 * 
	 * @see RequestError
	 */
	RequestError requestError=null;

	/**
	 * contains SMPP "submitSm" responses list
	 */
	private List<SMPPResponse> smppResponseList = null;

	/**
	 * return the HTTP response code returned from the server
	 */
	public int getHTTPResponseCode() { return httpResponseCode; }
	/**
	 * return the HTTP Content-Type returned from the server if available
	 */
	public String getContentType() { return contentType; }
	/**
	 * return the server generated error response (from the JSON based error response)
	 */
	public RequestError getRequestError() { return requestError; }

	/**
	 * set the stored HTTP response code
	 * @param httpResponseCode sets the stored HTTP response code
	 */
	public void setHTTPResponseCode(int httpResponseCode) { this.httpResponseCode=httpResponseCode; }
	/**
	 * set the HTTP Content-Type header returned by the server
	 * @param contentType sets the stored HTTP Content-Type header
	 */
	public void setContentType(String contentType) { this.contentType=contentType; }
	/**
	 * set the contents of the error response
	 * @param requestError sets the contents of the error response
	 * @see RequestError
	 */
	public void setRequestError(RequestError requestError) { this.requestError=requestError; }

	/**
	 * the contents of the HTTP 'Location' header response if available
	 */
	String location=null;
	/** 
	 * return the HTTP location field returned form the server
	 */
	public String getLocation() { return location; }
	/** 
	 * set the HTTP location field
	 * @param location contents of the HTTP location header
	 */
	public void setLocation(String location) { this.location=location; }

	/**
	 * resourceReference contains a URL uniquely identifying a successful request to send an SMS message
	 */
	ResourceReference resourceReference=null;
	/**
	 * return resourceReference - a URL uniquely identifying a successful request to send an SMS message
	 */
	public ResourceReference getResourceReference() { return resourceReference; }
	/**
	 * set resourceReference, the URL uniquely identifying a successful request to send an SMS message. This is called internally to set the contents according to the JSON response.
	 * @param resourceReference
	 */
	public void setResourceReference(ResourceReference resourceReference) { this.resourceReference=resourceReference; }

	/**
	 * add sent SMPP message response
	 * @param recipientAddress
	 * @param response
	 */
	public void addSMPPResponse(String recipientAddress, String response) {
		if (smppResponseList == null) {
			smppResponseList = new ArrayList<SMPPResponse>();
		}
		smppResponseList.add(new SMPPResponse(recipientAddress, response));
	}

	/**
	 * get sent SMPP messages response list 
	 * @return List<SMPPResponse> - 'recipient', 'response' pair list
	 */
	public List<SMPPResponse> getSMPPResponseList() {
		return smppResponseList;
	}

	/** 
	 * generate a textual representation of the SMSSendResponse including all nested elements and classes 
	 */
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if (httpResponseCode > 0) {
			buffer.append("httpResponseCode = "+httpResponseCode);
			buffer.append(", contentType = "+contentType);
		}
		if (requestError!=null) {
			if (buffer.length() > 0) buffer.append(", ");
			buffer.append("requestError = {");
			buffer.append(requestError.toString());
			buffer.append("}");
		}
		if (resourceReference!=null) {
			if (buffer.length() > 0) buffer.append(", ");
			buffer.append("resourceReference = {");
			buffer.append(resourceReference.toString());
			buffer.append("}");
		}
		if (smppResponseList!=null) {
			if (buffer.length() > 0) buffer.append(", ");
			buffer.append("smppResponseList = {");
			for (SMPPResponse smppReponse : smppResponseList) {
				buffer.append(smppReponse.toString());
				buffer.append(";");
			}
			buffer.append("}");
		}
		return buffer.toString();
	}
}
