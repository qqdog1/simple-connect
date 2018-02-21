package name.qd.simpleConnect.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.client.receiver.ClientReceivingThread;
import name.qd.simpleConnect.common.enumeration.REJ_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.common.sender.SendingQManager;

public class ClientThread extends Thread {
	private ClientConfigLoader configLoader;
	private Logger log = LoggerFactory.getLogger(ClientThread.class);
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private boolean initStatus = true;
	private boolean connectStatus = false;
	
	private boolean runFlag = false;
	private String clientName;
	
	private SimpleConnectReceiver receiver;
	private SendingQManager sendingQManager;
	private ReceivingQManager receivingQManager;
	private ClientReceivingThread clientReceivingThread;
	
	public ClientThread(String configPath, SimpleConnectReceiver receiver, String clientName) {
		this.clientName = clientName;
		this.receiver = receiver;
		initConfigAndLogger(configPath);
	}
	
	public void connectToServer() {
		if(!initStatus) {
			log.error("Client init failed, not allow to connect to server. Check the logs before this line.");
			return;
		}
		
		if(connectServer()) {
			initReceiver();
			this.start();
			log.info("Connected to Server.");
			return;
		} else {
			log.warn("Connect to Server Failed.");
		}
	}
	
	private void initConfigAndLogger(String configPath) {
		try {
			configLoader = ClientConfigLoader.getInstance();
			configLoader.init(configPath);
			log.info("Config loaded.");
		} catch (Exception e) {
			initStatus = false;
			log.error("Simple Connect Client init failed. Check the config.", e);
			return;
		}
	}
	
	private void initReceiver() {
		if(receiver != null) {
			receivingQManager = new ReceivingQManager(configLoader.getReceivingQueueSize(), configLoader.getHeartbeatInterval(), configLoader.getHeartbeatCount());
			clientReceivingThread = new ClientReceivingThread(this, receivingQManager);
		} else {
			initStatus = false;
			log.error("ServerSocketReceiver can't be null.");
		}
	}
	
	private boolean connectServer() {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(configLoader.getServerIp(), configLoader.getServerPort()));
			log.info("Connect to Server. IP:[{}], Port:[{}]", configLoader.getServerIp(), configLoader.getServerPort());
		
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			runFlag = true;
		} catch (IOException e) {
			log.error("Connect to server failed.", e);
			return false;
		}
		return true;
	}
	
	public void run() {
		while(runFlag) {
			try {
				PackVo vo = SimpleConnectDataPacker.unpackingData(inputStream);
				receivingQManager.add(vo);
			} catch (IllegalStateException e) {
				log.error("Receiving Queue Full.", e);
			} catch (IOException e) {
				// TODO notify ap
				log.error("Client thread run failed.", e);
				runFlag = false;
				disconnect();
			}
		}
	}
	
	public void receiveConfirm() {
		if(!connectStatus) {
			initSendingQManager();
			connectStatus = true;
		} else {
			// TODO error
		}
	}
	
	private void initSendingQManager() {
		sendingQManager = new SendingQManager(configLoader.getSendingQueueSize(), configLoader.getHeartbeatInterval(), outputStream, clientName);
	}
	
	public boolean send(byte[] data) {
		return sendingQManager.putQueue(data);
	}
	
	public void receiveData(byte[] data) {
		receiver.onMessage(clientName, data);
	}
	
	public void receiveReject(byte[] data) {
		this.runFlag = false;
		
		REJ_CodeEnum rej_CodeEnum = REJ_CodeEnum.getREJ_CodeEnum(data);
		switch(rej_CodeEnum) {
		case SAME_IP_FULL:
			log.error("Receive Reject from Server. Cause:[{}]", rej_CodeEnum.name());
			break;
		default:
			log.error("Receiver Reject from Server with unknow reason.");
			break;
		}
		
		disconnect();
		
		// TODO notify ap
	}
	
	public void heartbeatTimeout() {
		log.error("Can't receive any message from server.");
		disconnect();
		
		// TODO notify ap
	}
	
	public void disconnect() {
		try {
			if(sendingQManager != null) {
				sendingQManager.closeSendingQThread();
			}
			clientReceivingThread.closeReceivingThread();
			socket.close();
			log.info("Disconnect with server.");
			
			runFlag = false;
			connectStatus = false;
		} catch (IOException e) {
			log.error("Disconnect with server failed.", e);
		}
	}
	
	public boolean isConnect() {
		return connectStatus;
	}
}
