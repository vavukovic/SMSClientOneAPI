package sms.smpp.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public class KeepAliveGuard {
	private static final Logger logger = LoggerFactory.getLogger(KeepAliveGuard.class);
	static public final int ENQUIRE_TIMEOUT = 10000;

	private ScheduledExecutorService fScheduler;

	public KeepAliveGuard() {
		super();
	}

	public void Start(long interval, SmppSession session) {
		this.Stop();

		if (interval <= 0)
			return;

		fScheduler = Executors.newScheduledThreadPool(1);

		// fire first keepalive after 0.5 sec and then each interval milliseconds
		Runnable keepAliveTask = new KeepAliveTask(session);
		fScheduler.scheduleWithFixedDelay(keepAliveTask, 500, interval, TimeUnit.MILLISECONDS);
	}

	public void Stop() {
		if (fScheduler != null) {
			fScheduler.shutdown();
		}
	}

	private static final class KeepAliveTask implements Runnable {
		private SmppSession session;
		public KeepAliveTask(SmppSession session) {
			this.session = session;
		}
		@SuppressWarnings({ "rawtypes", "unused" })
		public void run() {
			try {
				WindowFuture<Integer, PduRequest, PduResponse> future;

				future = session.sendRequestPdu(new EnquireLink(), ENQUIRE_TIMEOUT, true);
				if (!future.await()) {     
					logger.error("KeepAlive ERROR: Failed to receive enquire_link_resp within specified time");
				} else if (future.isSuccess()) {   
					EnquireLinkResp enquireLinkResp = (EnquireLinkResp)future.getResponse();
					//logger.info("KeepAlive Ok response: [" + enquireLinkResp.getCommandStatus() + "=" + enquireLinkResp.getResultMessage() + "]");  
				} else {    
					logger.error("KeepAlive ERROR: Failed to properly receive enquire_link_resp: " + future.getCause());
				}
			} catch (RecoverablePduException e) {
				e.printStackTrace();
			} catch (UnrecoverablePduException e) {
				e.printStackTrace();
			} catch (SmppTimeoutException e) {
				e.printStackTrace();
			} catch (SmppChannelException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}
}
