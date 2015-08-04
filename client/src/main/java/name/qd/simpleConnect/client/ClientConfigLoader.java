package name.qd.simpleConnect.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class ClientConfigLoader {
	private static ClientConfigLoader instance = new ClientConfigLoader();
	
	private static final String LOG4J_CONFIG_PATH = "Log4jConfigPath";
	
	private static final String SERVER_IP = "ServerIp";
	private static final String SERVER_PORT = "ServerPort";
	private static final String HEARTBEAT_INTERVAL = "Heartbeat-Interval";
	private static final String HEARTBEAT_COUNT = "Heartbeat-Count";
	
	private static final String SENDING_QUEUE_SIZE = "SendingQueue-Size";
	private static final String RECEIVING_QUEUE_SIZE = "ReceivingQueue-Size";
	
	private String sConfigPath;
	
	private Properties properties;
	
	private String sServerIp;
	private int iServerPort;
	private int iHeartbeatInterval;
	private int iHeartbeatCount;
	
	private int iSendingQueueSize;
	private int iReceivingQueueSize;
	
	private ClientConfigLoader() {
	}

	public static ClientConfigLoader getInstance() {
		return instance;
	}
	
	public void init(String sConfigPath) throws FileNotFoundException, NumberFormatException, IOException, Exception {
		this.sConfigPath = sConfigPath;
		
		loadProperties();
		
		setLogger();
		
		readServerIpfromConfig();
		
		readServerPortfromConfig();
		
		readHeartbeatIntervalfromConfig();

		readHeartbeatCountfromConfig();
		
		readSendingQueueSizefromConfig();
		
		readReceivingQueueSizefromConfig();
	}
	
	private void loadProperties() throws FileNotFoundException, IOException {
		properties = new Properties();
		FileInputStream fIn = new FileInputStream(sConfigPath);
		properties.load(fIn);
		fIn.close();
	}
	
	private void setLogger() {
		String sLogConfigPath = properties.getProperty(LOG4J_CONFIG_PATH);
		PropertyConfigurator.configure(sLogConfigPath);
	}
	
	private void readServerIpfromConfig() throws Exception {
		sServerIp = properties.getProperty(SERVER_IP);
		if(sServerIp == null) {
			throw new Exception(getExceptionDesc(SERVER_IP));
		}
	}
	
	private void readServerPortfromConfig() throws Exception {
		String sServerPort = properties.getProperty(SERVER_PORT);
		if(sServerPort == null) {
			throw new Exception(getExceptionDesc(SERVER_PORT));
		}
		
		try {
			iServerPort = Integer.parseInt(sServerPort);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readHeartbeatIntervalfromConfig() throws Exception {
		String sHeartbeatInterval = properties.getProperty(HEARTBEAT_INTERVAL);
		if(sHeartbeatInterval == null) {
			throw new Exception(getExceptionDesc(HEARTBEAT_INTERVAL));
		}
		
		try {
			iHeartbeatInterval = Integer.parseInt(sHeartbeatInterval);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readHeartbeatCountfromConfig() throws Exception {
		String sHeartbeatCount = properties.getProperty(HEARTBEAT_COUNT);
		if(sHeartbeatCount == null) {
			throw new Exception(getExceptionDesc(HEARTBEAT_COUNT));
		}
		
		try {
			iHeartbeatCount = Integer.parseInt(sHeartbeatCount);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readSendingQueueSizefromConfig() throws Exception {
		String sSendingQueueSize = properties.getProperty(SENDING_QUEUE_SIZE);
		if(sSendingQueueSize == null) {
			throw new Exception(getExceptionDesc(SENDING_QUEUE_SIZE));
		}
		
		try {
			iSendingQueueSize = Integer.parseInt(sSendingQueueSize);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readReceivingQueueSizefromConfig() throws Exception {
		String sReceivingQueueSize = properties.getProperty(RECEIVING_QUEUE_SIZE);
		if(sReceivingQueueSize == null) {
			throw new Exception(getExceptionDesc(RECEIVING_QUEUE_SIZE));
		}
		
		try {
			iReceivingQueueSize = Integer.parseInt(sReceivingQueueSize);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	public String getServerIp() {
		return sServerIp;
	}
	
	public int getServerPort() {
		return iServerPort;
	}
	
	public int getHeartbeatInterval() {
		return iHeartbeatInterval;
	}
	
	public int getHeartbeatCount() {
		return iHeartbeatCount;
	}
	
	public int getSendingQueueSize() {
		return iSendingQueueSize;
	}
	
	public int getReceivingQueueSize() {
		return iReceivingQueueSize;
	}
	
	private String getExceptionDesc(String sTag) {
		return sTag + "must set in Config, check the config. ConfigPath:[" + sConfigPath + "]";
	}
}
