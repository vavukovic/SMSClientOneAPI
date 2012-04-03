package sms.common.config;

import java.io.File;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import sms.common.exceptiontype.ConfigException;
import sms.common.impl.SMSClient.SenderType;
import sms.oneapi.config.OneAPIConfig;
import sms.smpp.config.SmppConfig;

@JsonSerialize(include = Inclusion.NON_NULL)
public class MainConfig  {

	private SmppConfig smppConfig;
	private OneAPIConfig oneAPIConfig;	
	private SenderType senderType = SenderType.ONEAPI;
	private String configFileName = "client.cfg";

	public MainConfig() {  
		super();
	}
	
	public void saveToConfigFile() throws ConfigException  {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(this.configFileName), this);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	public void loadFromConfigFile() throws ConfigException {
		MainConfig tmpConfiguration = new MainConfig();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_COMMENTS, true);
		try {
			tmpConfiguration = mapper.readValue(new File(this.configFileName), MainConfig.class);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage(), e);
		}

		this.senderType = tmpConfiguration.senderType;	
		this.oneAPIConfig = tmpConfiguration.oneAPIConfig;
		this.smppConfig = tmpConfiguration.smppConfig;
	}

	@JsonIgnore
	public String getConfigFileName() {
		return configFileName;
	}
	
	@JsonIgnore
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	
	@JsonProperty("senderType")
	public SenderType getSenderType() {
		return senderType;
	}

	@JsonProperty("senderType")
	public void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}

	@JsonProperty("oneAPIConfig")
	public OneAPIConfig getOneAPI() {
		if (oneAPIConfig == null) {
			oneAPIConfig = new OneAPIConfig();
		}
		return oneAPIConfig;
	}

	@JsonProperty("oneAPIConfig")
	public void setOneAPI(OneAPIConfig value) {
		this.oneAPIConfig = value;
	}
	
	@JsonProperty("smppConfig")
	public SmppConfig getSmpp() {
		if (smppConfig == null) {
			smppConfig = new SmppConfig();
		}
		return smppConfig;
	}

	@JsonProperty("smppConfig")
	public void setSmpp(SmppConfig value) {
		this.smppConfig = value;
	}
}

