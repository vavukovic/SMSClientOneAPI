package sms.common.response;

import sms.common.exceptiontype.RequestError;
import sms.common.model.DeliveryInfoList;

/**
 * The full response from the OneAPI server for a request to check SMS Delivery Status
 */
public class SMSSendDeliveryStatusResponse implements java.io.Serializable {	
	private static final long serialVersionUID = -7840104931971343018L;

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


	String jsonResponse = null;

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
	 * contains a list of one or more message delivery status according to the number of recipients for the SMS message 
	 */
	DeliveryInfoList deliveryInfoList=null;
	/**
	 * get the list of one or more message delivery status according to the number of recipients for the SMS message
	 * @return DeliveryInfoList
	 */
	public DeliveryInfoList getDeliveryInfoList() { return deliveryInfoList; }
	/**
	 * set list of one or more message delivery status according to the number of recipients for the SMS message. This is called internally to set the contents according to the JSON response.
	 * @param deliveryInfoList
	 */
	public void setDeliveryInfoList(DeliveryInfoList deliveryInfoList) { this.deliveryInfoList=deliveryInfoList; }

	/** 
	 * generate a textual representation of the SMSSendDeliveryStatusResponse including all nested elements and classes 
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
		if (deliveryInfoList!=null) {
			if (buffer.length() > 0) buffer.append(", ");
			buffer.append("deliveryInfoList = {");
			buffer.append(deliveryInfoList.toString());
			buffer.append("}");
		}
		return buffer.toString();
	}
}
