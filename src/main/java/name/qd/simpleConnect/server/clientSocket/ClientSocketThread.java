package name.qd.simpleConnect.server.clientSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;
import name.qd.simpleConnect.common.sender.SendingQManager;
import name.qd.simpleConnect.server.ServerConfigLoader;
import name.qd.simpleConnect.server.clientSocket.receiver.ReceiverThread;

import org.apache.log4j.Logger;

public class ClientSocketThread extends Thread {
	
	private Logger mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
	
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private ServerConfigLoader serverConfigLoader;
	
	private SimpleConnectReceiver receiver;
	private SendingQManager sendingQManager;
	private ReceivingQManager receivingQManager;
	private ReceiverThread receiverThread;
	
	private boolean bRunFlag;
	private String sKey;
	
	public ClientSocketThread(Socket socket, SimpleConnectReceiver receiver, String sKey, ServerConfigLoader serverConfigLoader) {
		try {
			this.socket = socket;
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			this.serverConfigLoader = serverConfigLoader;

			this.receiver = receiver;
			this.sKey = sKey;
			
			initReceivingQManager();
			initSendingQManager();
			
			bRunFlag = true;
		} catch (IOException e) {
			mLogger.error(e);
		}
	}
	
	private void initReceivingQManager() {
		receivingQManager = new ReceivingQManager(serverConfigLoader.getReceivingQueueSize(), serverConfigLoader.getHeartbeatInterval(), serverConfigLoader.getHeartbeatCount());
		receiverThread = new ReceiverThread(this, receivingQManager, sKey);
	}
	
	private void initSendingQManager() {
		sendingQManager = new SendingQManager(mLogger, ServerConfigLoader.getInstance().getSendingQueueSize(), ServerConfigLoader.getInstance().getHeartbeatInterval(), outputStream, sKey);
	}
	
	public void startClientSocketThread() {
		this.start();
	}
	
	public void run() {
		while(bRunFlag) {
			try {
				PackVo vo = SimpleConnectDataPacker.unpackingData(inputStream);
				receivingQManager.add(vo);
			} catch (IllegalStateException e) {
				mLogger.error("Receiving Queue Full.", e);
			} catch (IOException e) {
				mLogger.error(e);
				bRunFlag = false;
				disconnect();
				mLogger.info("Close Connect. Key:[" + sKey + "]");
			}
		}
	}
	
	public boolean send(byte[] bData) {
		return sendingQManager.putQueue(bData);
	}
	
	public void receiveData(byte[] bData) {
		receiver.onMessage(sKey, bData);
	}
	
	public void heartbeatTimeout() {
		mLogger.error("Can't receive any message from Client. Key:[" + sKey + "]");
		disconnect();
	}
	
	public void sendConfirm() {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.CONFIRM);
		vo.setData(new byte[0]);
		sendPackVo(vo);
	}
	
	public void sendReject(byte[] bRej_Code) {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.REJECT);
		vo.setData(bRej_Code);
		sendPackVo(vo);
	}
	
	private boolean sendPackVo(PackVo vo) {
		try {
			outputStream.write(SimpleConnectDataPacker.packingData(vo));
		} catch (IOException e) {
			mLogger.error(e);
			return false;
		}
		return true;
	}
	
	public void disconnect() {
		try {
			sendingQManager.closeSendingQThread();
			receiverThread.closeReceivingThread();
			
			if(sKey != null) {
				ClientSocketThreadManager.getInstance().remove(sKey);
				LoginKeyControl.getInstance().removeLoginKey(sKey);
			}
			
			socket.close();
		} catch (IOException e) {
			mLogger.error(e);
		}
	}
}
