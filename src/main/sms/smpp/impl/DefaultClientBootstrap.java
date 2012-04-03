package sms.smpp.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import com.cloudhopper.smpp.impl.DefaultSmppClient;

public class DefaultClientBootstrap {
	private ThreadPoolExecutor executor = null;
	private ScheduledThreadPoolExecutor monitorExecutor = null;
	private DefaultSmppClient clientBootstrap = null;

	public DefaultSmppClient get() {
		return this.clientBootstrap;
	}

	protected void create() throws Exception {
		// setup 3 things required for any session we plan on creating

		// for monitoring thread use, it's preferable to create your own
		// instance
		// of an executor with Executors.newCachedThreadPool() and cast it
		// to ThreadPoolExecutor
		// this permits exposing thinks like executor.getActiveCount() via
		// JMX possible
		// no point renaming the threads in a factory since underlying Netty
		// framework does not easily allow you to customize your thread
		// names
		this.executor = (ThreadPoolExecutor) Executors
				.newCachedThreadPool();

		// to enable automatic expiration of requests, a second scheduled
		// executor
		// is required which is what a monitor task will be executed with -
		// this
		// is probably a thread pool that can be shared with between all
		// client bootstraps
		this.monitorExecutor = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(1, new ThreadFactory() {
					private AtomicInteger sequence = new AtomicInteger(0);

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("SmppClientSessionWindowMonitorPool-"
								+ sequence.getAndIncrement());
						return t;
					}
				});

		// a single instance of a client bootstrap can technically be shared
		// between any sessions that are created (a session can go to any
		// different
		// number of SMSCs) - each session created under
		// a client bootstrap will use the executor and monitorExecutor set
		// in its constructor - just be *very* careful with the
		// "expectedSessions"
		// value to make sure it matches the actual number of total
		// concurrent
		// open sessions you plan on handling - the underlying netty library
		// used for NIO sockets essentially uses this value as the max
		// number of
		// threads it will ever use, despite the "max pool size", etc. set
		// on
		// the executor passed in here
		clientBootstrap = new DefaultSmppClient(
				Executors.newCachedThreadPool(), 1, monitorExecutor);
	}

	protected void destroy() throws Exception {
		if (clientBootstrap != null) {
			this.clientBootstrap.destroy();
			this.clientBootstrap = null;
		}
		if (executor != null) {
			this.executor.shutdownNow();
			this.executor = null;
		}
		if (monitorExecutor != null) {
			this.monitorExecutor.shutdownNow();
			this.monitorExecutor = null;
		}	
	}
}