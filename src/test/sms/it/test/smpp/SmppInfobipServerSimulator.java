package sms.it.test.smpp;
import java.io.IOException;
import org.infobip.smscsim.SmartSimulator;
import org.infobip.smscsim.SmartSimulatorConfiguration;


public class SmppInfobipServerSimulator {
	
	public SmppInfobipServerSimulator() {
		super();
	}
	
	public static SmartSimulator create(int port) throws IOException {
		SmartSimulatorConfiguration configuration = new SmartSimulatorConfiguration();
		configuration.setIpAddress("0.0.0.0");
		configuration.setPorts(port);
		return new SmartSimulator(configuration);
	}
	
	
}
