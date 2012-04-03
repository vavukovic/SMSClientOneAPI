package sms.oneapi.response;

import sms.common.exceptiontype.RequestError;
import sms.oneapi.model.DeliveryReceiptSubscription;

/**
 * The full response from the OneAPI server for a request to subscribe to SMS Delivery Receipts
 */
public class SMSDeliveryReceiptSubscriptionResponse implements java.io.Serializable {
	
	private static final long serialVersionUID = 1385695000469468349L;
	
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
	 * the deliveryReceiptSubscription response contains a URL identifying the subscription along with callback information (notification URL and callback data)
	 */
	DeliveryReceiptSubscription deliveryReceiptSubscription=null;
	/**
	 * return the contents of the deliveryReceiptSubscription response 
	 * @return DeliveryReceiptSubscription
	 */
	public DeliveryReceiptSubscription getDeliveryReceiptSubscription() { return deliveryReceiptSubscription; }
	/**
	 * set the deliveryReceiptSubscription response. This is called internally to set the contents according to the JSON response.
	 * @param deliveryReceiptSubscription
	 */
	public void setDeliveryReceiptSubscription(DeliveryReceiptSubscription deliveryReceiptSubscription) { this.deliveryReceiptSubscription=deliveryReceiptSubscription; } 

	/** 
	 * generate a textual representation of the SMSDeliveryReceiptSubscriptionResponse including all nested elements and classes 
	 */
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("httpResponseCode = "+httpResponseCode);
		buffer.append(", contentType = "+contentType);
		if (requestError!=null) {
			buffer.append(", requestError = {");
			buffer.append(requestError.toString());
			buffer.append("}");
		}
		if (deliveryReceiptSubscription!=null) {
			buffer.append(", deliveryReceiptSubscription = {");
			buffer.append(deliveryReceiptSubscription.toString());
			buffer.append("}");
		}
		return buffer.toString();
	}

	
}
