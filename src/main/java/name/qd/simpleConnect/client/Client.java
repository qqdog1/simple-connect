package name.qd.simpleConnect.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import name.qd.simpleConnect.client.receiver.ClientReceivingThread;
import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.common.enumeration.REJ_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.common.sender.SendingQManager;

import org.apache.log4j.Logger;

public class Client extends Thread {
	
	private ClientConfigLoader configLoader;
	private Logger mLogger;
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private boolean bInitStatus = true;
	private boolean bConnectStatus = false;
	
	private boolean bRunFlag = false;
	private String sClientName;
	
	private SimpleConnectReceiver receiver;
	private SendingQManager sendingQManager;
	private ReceivingQManager receivingQManager;
	private ClientReceivingThread clientReceivingThread;
	
	public Client(String sConfigPath, SimpleConnectReceiver receiver, String sClientName) {
		this.sClientName = sClientName;
		this.receiver = receiver;
		initConfigAndLogger(sConfigPath);
	}
	
	public void connectToServer() {
		if(!bInitStatus) {
			mLogger.error("Client init failed, not allow to connect to server. Check the logs before this line.");
			return;
		}
		
		if(connectServer()) {
			initReceiver();
			this.start();
			mLogger.info("Connected to Server.");
			return;
		} else {
			mLogger.warn("Connect to Server Failed.");
		}
	}
	
	private void initConfigAndLogger(String sConfigPath) {
		try {
			configLoader = ClientConfigLoader.getInstance();
			configLoader.init(sConfigPath);
			mLogger = Logger.getLogger(LogConstant.CLIENT_LOG);
			mLogger.info("Config loaded.");
		} catch (Exception e) {
			bInitStatus = false;
			mLogger = Logger.getLogger(LogConstant.CLIENT_LOG);
			mLogger.error("Simple Connect Client init failed. Check the config.", e);
			return;
		}
	}
	
	private void initReceiver() {
		if(receiver != null) {
			receivingQManager = new ReceivingQManager(configLoader.getReceivingQueueSize(), configLoader.getHeartbeatInterval(), configLoader.getHeartbeatCount());
			clientReceivingThread = new ClientReceivingThread(this, receivingQManager);
		} else {
			bInitStatus = false;
			mLogger.error("ServerSocketReceiver can't be null.");
		}
	}
	
	private boolean connectServer() {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(configLoader.getServerIp(), configLoader.getServerPort()));
			mLogger.info("Connect to Server. IP:[" + configLoader.getServerIp() + "], Port:[" + configLoader.getServerPort() + "]");
		
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			bRunFlag = true;
		} catch (IOException e) {
			mLogger.error(e);
			return false;
		}
		return true;
	}
	
	public void run() {
		while(bRunFlag) {
			try {
				PackVo vo = SimpleConnectDataPacker.unpackingData(inputStream);
				receivingQManager.add(vo);
			} catch (IllegalStateException e) {
				mLogger.error("Receiving Queue Full.", e);
			} catch (IOException e) {
				// TODO �i���٭n�� receiver ��AP���@�U�_�F
				mLogger.error(e);
				bRunFlag = false;
				disconnect();
			}
		}
	}
	
	public void receiveConfirm() {
		initSendingQManager();
		
		bConnectStatus = true;
	}
	
	private void initSendingQManager() {
		sendingQManager = new SendingQManager(mLogger, configLoader.getSendingQueueSize(), configLoader.getHeartbeatInterval(), outputStream, sClientName);
	}
	
	public boolean send(byte[] bData) {
		return sendingQManager.putQueue(bData);
	}
	
	public void receiveData(byte[] bData) {
		receiver.onMessage(sClientName, bData);
	}
	
	public void receiveReject(byte[] bData) {
		this.bRunFlag = false;
		
		REJ_CodeEnum rej_CodeEnum = REJ_CodeEnum.getREJ_CodeEnum(bData);
		switch(rej_CodeEnum) {
		case SAME_IP_FULL:
			mLogger.error("Receive Reject from Server. Cause:[" + rej_CodeEnum + "]");
			break;
		default:
			mLogger.error("Receiver Reject from Server with unknow reason.");
			break;
		}
		
		disconnect();
		
		// TODO �i���٭n�� receiver ��AP���@�U�_�F
	}
	
	public void heartbeatTimeout() {
		mLogger.error("Can't receive any message from server.");
		disconnect();
		
		// TODO �i���٭n�� receiver ��AP���@�U�_�F
	}
	
	private void disconnect() {
		try {
			if(sendingQManager != null) {
				sendingQManager.closeSendingQThread();
			}
			clientReceivingThread.closeReceivingThread();
			socket.close();
			mLogger.info("Disconnect with server.");
			
			bRunFlag = false;
			bConnectStatus = false;
		} catch (IOException e) {
			mLogger.error(e);
		}
	}
	
	public boolean isConnect() {
		return bConnectStatus;
	}
}
