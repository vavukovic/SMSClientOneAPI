package sms.common.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.event.EventListenerList;
import org.codehaus.jackson.map.ObjectMapper;

import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.RequestError;
import sms.common.model.DeliveryReportListener;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.smpp.impl.SmppSessionWrapper.DLRType;


public class OneAPIPushServer implements Runnable {
	private int port = 8080;
	private boolean running = true;
	private ServerSocket server;
	//private List<String> requests = new ArrayList<String>();
	private String postRequest;
	private String response = "OK";
	private EventListenerList deliveryReportListenerList = null;
	
	public OneAPIPushServer( int port ) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			if ( server == null)
				server = new ServerSocket( this.port );
				
			while ( running ) {
				Socket connection = null;
				try {
					connection = server.accept();
					// get request from client
					String request = getRequest( connection );
					//requests.add( request );
					
					// get content (POST request)
					int postlen = parseContentLength(request);
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					char[] posted = new char[postlen];
					in.read(posted, 0, postlen);
					postRequest = new String(posted);
					System.out.println(postRequest);
					
					// return response to client
					OutputStream out = new BufferedOutputStream( connection.getOutputStream() );
									
//					if (request.startsWith("POST")) {
//						out.write("HTTP/1.1 201 OK\r\nConnection: close\r\n\r\n".getBytes());
//					} else if (request.startsWith("GET")) {
//						out.write("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nConnection: close\r\n\r\n".getBytes());
//					} else if (request.startsWith("DELETE")) {
//						out.write("HTTP/1.1 204 OK\r\nConnection: close\r\n\r\n".getBytes());
//					}
							
					out.write(response.getBytes());
					out.flush(  );   	
					in.close();
					
		
				 	SMSSendDeliveryStatusResponse response = new SMSSendDeliveryStatusResponse();
					ObjectMapper mapper = new ObjectMapper();
					try {
						response = mapper.readValue(postRequest, SMSSendDeliveryStatusResponse.class);
					} catch (Exception e) {
						response.setRequestError(new RequestError(RequestError.SERVICEEXCEPTION, "SVCJAVA", e.getMessage(), e.getClass().getName()));		
						//response.setJsonResponse(postRequest);
					}	
					
					Object[] listeners = deliveryReportListenerList.getListenerList();		
					// Each listener occupies two elements - the first is the listener class
					// and the second is the listener instance
					for (int i=0; i<listeners.length; i+=2) {
						if (listeners[i]==DeliveryReportListener.class) {
							//TODO - Deserilaize JSON
							((DeliveryReportListener)listeners[i+1]).onDeliveryReportReceived(response, DLRType.sms);
						}
					}	
				}
				catch ( IOException ex ) {
					if ( server.isClosed() == false ) {
						ex.printStackTrace();
					}
				}
				finally {
					if ( connection != null ) {
						connection.close();
					}
				}
			}
		}
		catch ( IOException e ) {
			System.err.println( "Could not start server: " + e.getMessage() );
		}
	}
	
	/**
	 * Add OneAPI 'Delivery Reports' listener
	 * @param listener - (new DeliveryReportListener) 
	 */
	public void addDeliveryReportListener(DeliveryReportListener listener) throws DeliveryReportListenerException {
		if (this.deliveryReportListenerList == null) {
			this.deliveryReportListenerList = new EventListenerList();
		}		
		this.deliveryReportListenerList.add(DeliveryReportListener.class, listener);
	}

	private int parseContentLength(String request) {
		int position = request.indexOf("Content-Length:");
		if ( position == -1) {
			return 0;
		}
		String contentLength = request.substring(position);
		contentLength = contentLength.split(" ")[1];
		contentLength = contentLength.split("\r\n")[0];
		return Integer.valueOf(contentLength);
	}

	private String getRequest( Socket connection ) throws IOException {
		InputStream in = connection.getInputStream();
		StringBuilder request = new StringBuilder();
		int i;
		while( ( i = in.read() ) != -1 ) {
			request.append( (char) i );
			if( request.toString().endsWith( "\r\n\r\n" ) || request.toString().endsWith( "\n\n" ) ) {
				return request.toString();
			}
		}
		return request.toString();
	}

	public void release() {
		try {
			running = false;
			try {
				Thread.sleep( 100 );
			} catch (Exception ignore) {}

			if ( server != null && server.isClosed() == false )
				server.close();
			
			this.deliveryReportListenerList = null;
		}
		catch ( IOException e ) {
			System.err.println( "Could not release server:" + e.getMessage() );
		}
	}

//	public void setResponse(String response) {
//		this.response = response;
//	}

//	public List<String> getRequests() {
//		return requests;
//	}
}
