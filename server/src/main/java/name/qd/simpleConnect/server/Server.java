package name.qd.simpleConnect.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.common.enumeration.REJ_CodeEnum;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThread;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThreadManager;
import name.qd.simpleConnect.server.clientSocket.LoginKeyControl;

public class Server extends Thread {
	
	private Logger log = LoggerFactory.getLogger(Server.class);
	
	private ServerConfigLoader configLoader;
	
	private ServerSocket serverSocket;
	
	private boolean initStatus = true;
	
	private SimpleConnectReceiver receiver;
	private boolean runFlag = true;
	
	public Server(String configPath, SimpleConnectReceiver receiver) {
		initConfigAndLogger(configPath);
		
		initReceiver(receiver);
		
		bindSocket();
		
		initOther();
	}
	
	public void startServer() {
		this.start();
		log.info("Server started.");
	}
	
	private void initConfigAndLogger(String configPath) {
		try {
			configLoader = ServerConfigLoader.getInstance();
			configLoader.init(configPath);
			log.info("Config loaded.");
		} catch (Exception e) {
			initStatus = false;
			log.error("Simple Connect Server init failed. Check the config.", e);
			return;
		}
	}
	
	private void initReceiver(SimpleConnectReceiver receiver) {
		if(receiver != null) {
			this.receiver = receiver;
		} else {
			initStatus = false;
			log.error("ServerSocketReceiver can't be null.");
		}
	}
	
	private void bindSocket() {
		if(!initStatus) {
			log.error("Server init failed. Check the logs before this line.");
			return;
		}
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(configLoader.getServerIp(), configLoader.getServerPort()));
			log.info("Server bind. Address:[{}:{}]", configLoader.getServerIp(), configLoader.getServerPort());
		} catch (IOException e) {
			log.error("Bind socket failed.", e);
		}
	}
	
	private void initOther() {
		LoginKeyControl.getInstance();
	}
	
	public void run() {
		while(runFlag) {
			try {
				Socket socket = serverSocket.accept();
				String clientIp = socket.getInetAddress().getHostAddress();
				
				String key = LoginKeyControl.getInstance().getNewLoginKey(clientIp);
				ClientSocketThread clientSocketThread = new ClientSocketThread(socket, receiver, key, configLoader);
				
				if(key == null) {
					log.warn("Client connected failed. Connections are full. Close Socket. IP:[{}]", clientIp);
					clientSocketThread.sendReject(REJ_CodeEnum.SAME_IP_FULL.getByteArray());
					clientSocketThread.disconnect();
				} else {
					log.info("Client:[{}] connected.", key);
					ClientSocketThreadManager.getInstance().add(key, clientSocketThread);
					clientSocketThread.sendConfirm();
					clientSocketThread.startClientSocketThread();
				}
			} catch (IOException e) {
				log.error("Server run failed.", e);
			}
		}
	}
	
	public boolean send(String clientName, byte[] data) {
		return ClientSocketThreadManager.getInstance().send(clientName, data);
	}
	
	public int send(byte[] data) {
		return ClientSocketThreadManager.getInstance().send(data);
	}
	
	public void stopServer() {
		runFlag = false;
		log.info("Stop Server.");
	}
}
