package name.qd.simpleConnect.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import name.qd.simpleConnect.common.constant.CodeConstant;

public class ServerConfigLoader {
	private static ServerConfigLoader instance = new ServerConfigLoader();

	private static final String SERVER_IP = "ServerIp";
	private static final String SERVER_PORT = "ServerPort";
	private static final String HEARTBEAT_INTERVAL = "Heartbeat-Interval";
	private static final String HEARTBEAT_COUNT = "Heartbeat-Count";
	
	private static final String SENDING_QUEUE_SIZE = "SendingQueue-Size";
	private static final String RECEIVING_QUEUE_SIZE = "ReceivingQueue-Size";
	
	private static final String ALLOW_SAME_IP_LOGIN = "AllowSameIpLogin";
	private static final String ALLOW_SAME_IP_COUNT = "AllowSameIpCount";
	
	private String configPath;
	
	private Properties properties;
	
	private String serverIp;
	private int serverPort;
	private int heartbeatInterval;
	private int heartbeatCount;
	private int sendingQueueSize;
	private int receivingQueueSize;
	
	private boolean isAllowSameIpLogin = false;
	private int allowSameIpCount;
	
	public static ServerConfigLoader getInstance() {
		return instance;
	}
	
	private ServerConfigLoader() {
	}
	
	public void init(String configPath) throws FileNotFoundException, NumberFormatException, IOException, Exception {
		this.configPath = configPath;
		
		loadProperties();
		
		readServerIpfromConfig();
		
		readServerPortfromConfig();
		
		readHeartbeatIntervalfromConfig();

		readHeartbeatCountfromConfig();
		
		readSendingQueueSizefromConfig();
		
		readReceivingQueueSizefromConfig();
		
		readIsAllowSameIpLoginfromConfig();
	}
	
	private void loadProperties() throws FileNotFoundException, IOException {
		properties = new Properties();
		FileInputStream fIn = new FileInputStream(configPath);
		properties.load(fIn);
		fIn.close();
	}
	
	private void readServerIpfromConfig() throws Exception {
		serverIp = properties.getProperty(SERVER_IP);
		if(serverIp == null) {
			throw new Exception(getExceptionDesc(SERVER_IP));
		}
	}
	
	private void readServerPortfromConfig() throws Exception {
		String serverPortString = properties.getProperty(SERVER_PORT);
		if(serverPortString == null) {
			throw new Exception(getExceptionDesc(SERVER_PORT));
		}
		
		try {
			serverPort = Integer.parseInt(serverPortString);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readHeartbeatIntervalfromConfig() throws Exception {
		String heartbeatIntervalString = properties.getProperty(HEARTBEAT_INTERVAL);
		if(heartbeatIntervalString == null) {
			throw new Exception(getExceptionDesc(HEARTBEAT_INTERVAL));
		}
		
		try {
			heartbeatInterval = Integer.parseInt(heartbeatIntervalString);
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
			heartbeatCount = Integer.parseInt(sHeartbeatCount);
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
			sendingQueueSize = Integer.parseInt(sSendingQueueSize);
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
			receivingQueueSize = Integer.parseInt(sReceivingQueueSize);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	private void readIsAllowSameIpLoginfromConfig() throws Exception {
		String sAllowSameIpLogin = properties.getProperty(ALLOW_SAME_IP_LOGIN);
		
		if(sAllowSameIpLogin == null) {
			throw new Exception(getExceptionDesc(ALLOW_SAME_IP_LOGIN));
		}
		
		if(CodeConstant.YES.equals(sAllowSameIpLogin)) {
			isAllowSameIpLogin = true;
			readAllowSameIpCountfromConfig();
		}
	}
	
	private void readAllowSameIpCountfromConfig() throws Exception {
		String sAllowSameIpCount = properties.getProperty(ALLOW_SAME_IP_COUNT);
		if(sAllowSameIpCount == null) {
			throw new Exception(getExceptionDesc(ALLOW_SAME_IP_COUNT));
		}
		
		try {
			allowSameIpCount = Integer.parseInt(sAllowSameIpCount);
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	public String getServerIp() {
		return serverIp;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}
	
	public int getHeartbeatCount() {
		return heartbeatCount;
	}
	
	public int getSendingQueueSize() {
		return sendingQueueSize;
	}
	
	public int getReceivingQueueSize() {
		return receivingQueueSize;
	}
	
	public boolean isAllowSameIpLogin() {
		return isAllowSameIpLogin;
	}
	
	public int getAllSameIpCount() {
		return allowSameIpCount;
	}
	
	private String getExceptionDesc(String sTag) {
		return sTag + "must set in Config, check the config. ConfigPath:[" + configPath + "]";
	}
}
