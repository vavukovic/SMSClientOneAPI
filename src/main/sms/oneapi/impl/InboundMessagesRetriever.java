package sms.oneapi.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.event.EventListenerList;
import sms.common.model.InboundMessageListener;
import sms.common.response.RetrieveSMSResponse;
import sms.oneapi.exceptiontype.RetrieveInboundMessagesException;

public class InboundMessagesRetriever {
	private ScheduledExecutorService fScheduler;


	public void Start(long interval, OneAPISender oneAPISender) {
		this.Stop();

		if (interval <= 0)
			return;

		fScheduler = Executors.newScheduledThreadPool(1);

		// fire first inbound sms pull attempt after 2 sec and then each interval milliseconds
		Runnable poller = new PollerTask(oneAPISender);
		fScheduler.scheduleWithFixedDelay(poller, 2000, interval, TimeUnit.MILLISECONDS);
	}

	public void Stop() {
		if (fScheduler != null) {
			fScheduler.shutdown();
		}
	}

	private static final class PollerTask implements Runnable {
		private OneAPIImpl oneAPIImpl;
		private OneAPISender oneAPISender;

		public PollerTask(OneAPISender oneAPISender) {
			this.oneAPISender = oneAPISender;
			this.oneAPIImpl = new OneAPIImpl(oneAPISender.getOneAPIConfig());;		
		}
		public void run() {	
			EventListenerList inboundMsglisteners = this.oneAPISender.getInboundMessageListeners();

			if ((inboundMsglisteners != null) && (inboundMsglisteners.getListenerCount() > 0)) {
				// use pull method to gather incoming messages
				RetrieveSMSResponse response = new RetrieveSMSResponse();
				try {
					response = oneAPIImpl.retrieveInboundMessages();
				} catch (RetrieveInboundMessagesException e) {
					response = e.getResponse();
				}												
				this.fireMessageRetrieved(response, inboundMsglisteners.getListenerList());				
			}
		}

		private void fireMessageRetrieved(RetrieveSMSResponse response, Object[] listeners) {
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2) {
				if (listeners[i]==InboundMessageListener.class) {
					((InboundMessageListener)listeners[i+1]).onMessageRetrieved(response);
				}
			}
		}
	}
}
