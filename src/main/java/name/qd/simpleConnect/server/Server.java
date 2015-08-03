package name.qd.simpleConnect.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.common.enumeration.REJ_CodeEnum;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThread;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThreadManager;
import name.qd.simpleConnect.server.clientSocket.LoginKeyControl;

import org.apache.log4j.Logger;

public class Server extends Thread {
	
	private Logger mLogger;
	
	private ServerConfigLoader configLoader;
	
	private ServerSocket serverSocket;
	
	private boolean bInitStatus = true;
	
	private SimpleConnectReceiver receiver;
	private boolean bRunFlag = true;
	
	public Server(String sConfigPath, SimpleConnectReceiver receiver) {
		initConfigAndLogger(sConfigPath);
		
		initReceiver(receiver);
		
		bindSocket();
		
		initOther();
	}
	
	public void startServer() {
		this.start();
		mLogger.info("Server started.");
	}
	
	private void initConfigAndLogger(String sConfigPath) {
		try {
			configLoader = ServerConfigLoader.getInstance();
			configLoader.init(sConfigPath);
			mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
			mLogger.info("Config loaded.");
		} catch (Exception e) {
			bInitStatus = false;
			mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
			mLogger.error("Simple Connect Server init failed. Check the config.", e);
			return;
		}
	}
	
	private void initReceiver(SimpleConnectReceiver receiver) {
		if(receiver != null) {
			this.receiver = receiver;
		} else {
			bInitStatus = false;
			mLogger.error("ServerSocketReceiver can't be null.");
		}
	}
	
	private void bindSocket() {
		if(!bInitStatus) {
			mLogger.error("Server init failed. Check the logs before this line.");
			return;
		}
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(configLoader.getServerIp(), configLoader.getServerPort()));
			mLogger.info("Server bind. Address:[" + configLoader.getServerIp() + ":" + configLoader.getServerPort() + "]");
		} catch (IOException e) {
			mLogger.error(e);
		}
	}
	
	private void initOther() {
		LoginKeyControl.getInstance();
	}
	
	public void run() {
		while(bRunFlag) {
			try {
				Socket socket = serverSocket.accept();
				String sClientIp = socket.getInetAddress().getHostAddress();
				
				String sKey = LoginKeyControl.getInstance().getNewLoginKey(sClientIp);
				ClientSocketThread clientSocketThread = new ClientSocketThread(socket, receiver, sKey, configLoader);
				
				if(sKey == null) {
					mLogger.warn("Client connected failed. Connections are full. Close Socket. IP:[" + sClientIp + "]");
					clientSocketThread.sendReject(REJ_CodeEnum.SAME_IP_FULL.getByteArray());
					clientSocketThread.disconnect();
				} else {
					mLogger.info("Client:[" + sKey + "] connected.");
					ClientSocketThreadManager.getInstance().add(sKey, clientSocketThread);
					clientSocketThread.sendConfirm();
					clientSocketThread.startClientSocketThread();
				}
			} catch (IOException e) {
				mLogger.error(e);
			}
		}
	}
	
	public boolean send(String sClientName, byte[] bData) {
		return ClientSocketThreadManager.getInstance().send(sClientName, bData);
	}
	
	public int send(byte[] bData) {
		return ClientSocketThreadManager.getInstance().send(bData);
	}
	
	public void stopServer() {
		bRunFlag = false;
		mLogger.info("Stop Server.");
	}
}
