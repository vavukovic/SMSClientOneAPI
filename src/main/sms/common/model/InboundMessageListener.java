package sms.common.model;

import java.util.EventListener;
import sms.common.response.RetrieveSMSResponse;

public interface InboundMessageListener extends EventListener {
	public void onMessageRetrieved(RetrieveSMSResponse retrievingResponse);
}
