package name.qd.simpleConnect.server.clientSocket.receiver;

import org.apache.log4j.Logger;

import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThread;

public class ReceiverThread extends Thread {
	
	private ClientSocketThread clientSocketThread;
	private ReceivingQManager receivingQManager;
	private String sKey;
	private boolean bRunFlag;
	
	private Logger mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
	
	public ReceiverThread(ClientSocketThread clientSocketThread, ReceivingQManager receivingQManager, String sKey) {
		this.clientSocketThread = clientSocketThread;
		this.receivingQManager = receivingQManager;
		this.sKey = sKey;
		bRunFlag = true;
		this.start();
	}

	public void run() {
		while(bRunFlag) {
			try {
				PackVo vo = receivingQManager.poll();
				
				if(vo == null) {
					if(bRunFlag) {
						clientSocketThread.heartbeatTimeout();
					}
					continue;
				}
				
				OP_CodeEnum op_CodeEnum = vo.getOP_CodeEnum();
				mLogger.debug("[" + sKey + "] Receive [" + op_CodeEnum + "] message from Client.");
				switch(op_CodeEnum) {
				case DATA:
					clientSocketThread.receiveData(vo.getData());
					break;
				case HEARTBEAT:
					break;
				default:
					break;
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeReceivingThread() {
		bRunFlag = false;
	}
}
