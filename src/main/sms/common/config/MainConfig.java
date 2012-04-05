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
	private final String CONFIG_FILE_NAME = "client.cfg";

	public MainConfig() {  
		super();
	}

	/**
	 * Save 'OneAPI' and 'SMPP' configuration data to 'client.cfg' file
	 * @throws ConfigException
	 */
	public void saveToConfigFile() throws ConfigException  {
		ObjectMapper mapper = new ObjectMapper();
		try {			
			File fileCfg = new File(this.CONFIG_FILE_NAME);
			if(!fileCfg.exists()){
				fileCfg.createNewFile();
			}
			
			mapper.writeValue(fileCfg, this);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	/**
	 * Load OneAPI' and 'SMPP' configuration data from 'client.cfg' file
	 * @throws ConfigException
	 */
	public void loadFromConfigFile() throws ConfigException {
		MainConfig tmpConfiguration = new MainConfig();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_COMMENTS, true);
		try {
			tmpConfiguration = mapper.readValue(new File(this.CONFIG_FILE_NAME), MainConfig.class);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage(), e);
		}

		this.senderType = tmpConfiguration.senderType;	
		this.oneAPIConfig = tmpConfiguration.oneAPIConfig;
		this.smppConfig = tmpConfiguration.smppConfig;
	}

	@JsonIgnore
	public String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	/**
	 * Get sender type configured to send SMS  (SenderType.ONEAPI, SenderType.SMPP)
	 * @return senderType
	 */
	@JsonProperty("senderType")
	public SenderType getSenderType() {
		return senderType;
	}

	/**
	 * Set sender type configured to send SMS  (SenderType.ONEAPI, SenderType.SMPP)
	 * @param senderType
	 */
	@JsonProperty("senderType")
	public void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}

	/**
	 * Get 'OneAPI' configuration data object
	 * @return
	 */
	@JsonProperty("oneAPIConfig")
	public OneAPIConfig getOneAPI() {
		if (oneAPIConfig == null) {
			oneAPIConfig = new OneAPIConfig();
		}
		return oneAPIConfig;
	}

	/**
	 * Set 'OneAPI' configuration data object
	 * @return oneAPIConfig
	 */
	@JsonProperty("oneAPIConfig")
	public void setOneAPI(OneAPIConfig value) {
		this.oneAPIConfig = value;
	}

	/**
	 *  Get 'SMPP' configuration data object
	 * @return smppConfig
	 */
	@JsonProperty("smppConfig")
	public SmppConfig getSmpp() {
		if (smppConfig == null) {
			smppConfig = new SmppConfig();
		}
		return smppConfig;
	}

	/**
	 * Set 'SMPP' configuration data object
	 * @param value
	 */
	@JsonProperty("smppConfig")
	public void setSmpp(SmppConfig value) {
		this.smppConfig = value;
	}
}

