package sms.smpp;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerConfiguration;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.SmppProcessingException;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;

public class SmppServerSimulator {
	private DefaultSmppServer smppServer = null;
	
    public void start(int port) throws Exception {
        //
        // setup 3 things required for a server
        //
        
        // for monitoring thread use, it's preferable to create your own instance
        // of an executor and cast it to a ThreadPoolExecutor from Executors.newCachedThreadPool()
        // this permits exposing thinks like executor.getActiveCount() via JMX possible
        // no point renaming the threads in a factory since underlying Netty 
        // framework does not easily allow you to customize your thread names
        ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        
        // to enable automatic expiration of requests, a second scheduled executor
        // is required which is what a monitor task will be executed with - this
        // is probably a thread pool that can be shared with between all client bootstraps
        ScheduledThreadPoolExecutor monitorExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private AtomicInteger sequence = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("SmppServerSessionWindowMonitorPool-" + sequence.getAndIncrement());
                return t;
            }
        });
        
        // create a server configuration
        SmppServerConfiguration configuration = new SmppServerConfiguration();
        configuration.setPort(port);
        configuration.setMaxConnectionSize(10);
        configuration.setNonBlockingSocketsEnabled(true);
        configuration.setDefaultRequestExpiryTimeout(30000);
        configuration.setDefaultWindowMonitorInterval(15000);
        configuration.setDefaultWindowSize(5);
        configuration.setDefaultWindowWaitTimeout(configuration.getDefaultRequestExpiryTimeout());
        configuration.setDefaultSessionCountersEnabled(true);
        configuration.setJmxEnabled(true);
        
        // create a server, start it up
        smppServer = new DefaultSmppServer(configuration, new DefaultSmppServerHandler(), executor, monitorExecutor);
        smppServer.start();
    }
    
    public void stop() {
    	smppServer.stop();
    }

    public static class DefaultSmppServerHandler implements SmppServerHandler {

        @SuppressWarnings("rawtypes")
		@Override
        public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, final BaseBind bindRequest) throws SmppProcessingException {
            // test name change of sessions
            // this name actually shows up as thread context....
            sessionConfiguration.setName("Application.SMPP." + sessionConfiguration.getSystemId());
            //throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL, null);
        }

        @Override
        public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) throws SmppProcessingException {
            // need to do something it now (flag we're ready)
            session.serverReady(new TestSmppSessionHandler(session));
            
            // invoke a delivery report push task
            // run it after 2 sec from now (server initialization). This simulates the real-world as much as possible.
            ScheduledExecutorService fScheduler = Executors.newScheduledThreadPool(1);
    		fScheduler.schedule(new DLRSender(session), 2000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void sessionDestroyed(Long sessionId, SmppServerSession session) {
            // print out final stats
            if (session.hasCounters()) {
                //logger.info(" final session rx-submitSM: {}", session.getCounters().getRxSubmitSM());
            }
            // make sure it's really shutdown
            session.destroy();
        }

    }

    public static class TestSmppSessionHandler extends DefaultSmppSessionHandler {
        private WeakReference<SmppSession> sessionRef;
        
        public TestSmppSessionHandler(SmppSession session) {
            this.sessionRef = new WeakReference<SmppSession>(session);
        }
        
        @SuppressWarnings({ "unused", "rawtypes" })
		@Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            SmppSession session = sessionRef.get();
            
            // mimic how long processing could take on a slower smsc
            try {
                //Thread.sleep(50);
            } catch (Exception e) { }

            return pduRequest.createResponse();
        }
    }
    
    private static final class DLRSender implements Runnable {
    	private WeakReference<SmppSession> sessionRef;
    	 
		public DLRSender(SmppServerSession session) {
			 this.sessionRef = new WeakReference<SmppSession>(session);
		}
		
		public void run() {
			SmppSession session = sessionRef.get();
			
			// send a dummy DLR message from server to client
			DeliveryReceipt dlr = new DeliveryReceipt();
	        dlr.setMessageId("1005");
	        dlr.setSubmitCount(1);
	        dlr.setDeliveredCount(1);
	        dlr.setSubmitDate(new DateTime(2012, 5, 23, 20, 39, 0, 0));
	        dlr.setDoneDate(new DateTime(2012, 5, 24, 23, 39, 20, 0));
	        dlr.setState(SmppConstants.STATE_DELIVERED);
	        dlr.setText("sometext");
			
			DeliverSm submit = new DeliverSm();
            try {
            	 submit.setShortMessage(dlr.toShortMessage().getBytes());
            	 submit.setEsmClass((byte) 4);
                 session.sendRequestPdu(submit, 30000, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
