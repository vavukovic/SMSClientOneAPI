package sms.oneapi.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.event.EventListenerList;

import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.model.DeliveryInfoList.DeliveryInfo;
import sms.common.model.DeliveryReportListener;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.smpp.impl.SmppSessionWrapper.DLRType;

public class DLRStatusRetriever {
	private ScheduledExecutorService fScheduler;
	// Delivery info status
	public static final String DELIVERYIMPOSSIBLE = "DeliveryImpossible";
	public static final String DELIVEREDTONETWORK = "DeliveredToNetwork";
	public static final String DELIVEREDTOTERMINAL = "DeliveredToTerminal";
	public static final String DELIVERYUNCERTAIN = "DeliveryUncertain";
	public static final String MESSAGEWAITING = "MessageWaiting";

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
			EventListenerList dlrStatusListeners = this.oneAPISender.getDeliveryReportListeners();

			if ((dlrStatusListeners != null) && (dlrStatusListeners.getListenerCount() > 0) && (oneAPISender.getResourceUrlList() != null)) {
				// use pull method to gather incoming messages
				SMSSendDeliveryStatusResponse response = new SMSSendDeliveryStatusResponse();

				for (String resourceUrl : oneAPISender.getResourceUrlList()) {
					try {
						response = oneAPIImpl.queryDeliveryStatusByUrl(resourceUrl);

						int count = 0;
						for (DeliveryInfo deliveryInfo : response.getDeliveryInfoList().getDeliveryInfo()) {
							if (deliveryInfo.getDeliveryStatus().equals(DELIVEREDTOTERMINAL) || 
									deliveryInfo.getDeliveryStatus().equals(DELIVERYIMPOSSIBLE) ||
									deliveryInfo.getDeliveryStatus().equals(DELIVERYUNCERTAIN))
							{
								count += 1;
							}	
						}

						if (count == response.getDeliveryInfoList().getDeliveryInfo().length) {
							oneAPISender.getResourceUrlList().remove(resourceUrl);
						}

					} catch (QueryDeliveryStatusException e) {
						response = e.getResponse();

					} finally {
						this.fireReportRetrieved(response, dlrStatusListeners.getListenerList());	
					}
				}
			}
		}

		private void fireReportRetrieved(SMSSendDeliveryStatusResponse response, Object[] listeners) {
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2) {
				if (listeners[i]==DeliveryReportListener.class) {
					((DeliveryReportListener)listeners[i+1]).onDeliveryReportReceived(response, DLRType.sms);
				}
			}
		}
	}
}
