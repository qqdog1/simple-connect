package name.qd.simpleConnect.server.clientSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.common.sender.SendingQManager;
import name.qd.simpleConnect.server.ServerConfigLoader;
import name.qd.simpleConnect.server.clientSocket.receiver.ReceiverThread;

public class ClientSocketThread extends Thread {
	private Logger log = LoggerFactory.getLogger(ClientSocketThread.class);
	
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private ServerConfigLoader serverConfigLoader;
	
	private SimpleConnectReceiver receiver;
	private SendingQManager sendingQManager;
	private ReceivingQManager receivingQManager;
	private ReceiverThread receiverThread;
	
	private boolean runFlag;
	private String key;
	
	public ClientSocketThread(Socket socket, SimpleConnectReceiver receiver, String key, ServerConfigLoader serverConfigLoader) {
		try {
			this.socket = socket;
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			this.serverConfigLoader = serverConfigLoader;

			this.receiver = receiver;
			this.key = key;
			
			initReceivingQManager();
			initSendingQManager();
			
			runFlag = true;
		} catch (IOException e) {
			log.error("ClientSocketThread exception.", e);
		}
	}
	
	private void initReceivingQManager() {
		receivingQManager = new ReceivingQManager(serverConfigLoader.getReceivingQueueSize(), serverConfigLoader.getHeartbeatInterval(), serverConfigLoader.getHeartbeatCount());
		receiverThread = new ReceiverThread(this, receivingQManager, key);
	}
	
	private void initSendingQManager() {
		sendingQManager = new SendingQManager(ServerConfigLoader.getInstance().getSendingQueueSize(), ServerConfigLoader.getInstance().getHeartbeatInterval(), outputStream, key);
	}
	
	public void startClientSocketThread() {
		this.start();
	}
	
	public void run() {
		while(runFlag) {
			try {
				PackVo vo = SimpleConnectDataPacker.unpackingData(inputStream);
				receivingQManager.add(vo);
			} catch (IllegalStateException e) {
				log.error("Receiving Queue Full.", e);
			} catch (IOException e) {
				log.error("unpacking data failed.", e);
				runFlag = false;
				disconnect();
				log.info("Close Connect. Key:[{}]", key);
			}
		}
	}
	
	public boolean send(byte[] data) {
		return sendingQManager.putQueue(data);
	}
	
	public void receiveData(byte[] data) {
		receiver.onMessage(key, data);
	}
	
	public void heartbeatTimeout() {
		log.error("Can't receive any message from Client. Key:[{}]", key);
		disconnect();
	}
	
	public void sendConfirm() {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.CONFIRM);
		vo.setData(new byte[0]);
		sendPackVo(vo);
	}
	
	public void sendReject(byte[] rej_Code) {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.REJECT);
		vo.setData(rej_Code);
		sendPackVo(vo);
	}
	
	private boolean sendPackVo(PackVo vo) {
		try {
			outputStream.write(SimpleConnectDataPacker.packingData(vo));
		} catch (IOException e) {
			log.error("Send pack vo failed.", e);
			return false;
		}
		return true;
	}
	
	public void disconnect() {
		try {
			sendingQManager.closeSendingQThread();
			receiverThread.closeReceivingThread();
			
			if(key != null) {
				ClientSocketThreadManager.getInstance().remove(key);
				LoginKeyControl.getInstance().removeLoginKey(key);
			}
			
			socket.close();
		} catch (IOException e) {
			log.error("Disconnect failed.", e);
		}
	}
}
