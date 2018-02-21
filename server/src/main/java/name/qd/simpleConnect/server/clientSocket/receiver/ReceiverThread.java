package name.qd.simpleConnect.server.clientSocket.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;
import name.qd.simpleConnect.server.clientSocket.ClientSocketThread;

public class ReceiverThread extends Thread {
	
	private ClientSocketThread clientSocketThread;
	private ReceivingQManager receivingQManager;
	private String key;
	private boolean runFlag;
	
	private Logger log = LoggerFactory.getLogger(ReceiverThread.class);
	
	public ReceiverThread(ClientSocketThread clientSocketThread, ReceivingQManager receivingQManager, String key) {
		this.clientSocketThread = clientSocketThread;
		this.receivingQManager = receivingQManager;
		this.key = key;
		runFlag = true;
		this.start();
	}

	public void run() {
		while(runFlag) {
			try {
				PackVo vo = receivingQManager.poll();
				
				if(vo == null) {
					if(runFlag) {
						clientSocketThread.heartbeatTimeout();
					}
					continue;
				}
				
				OP_CodeEnum op_CodeEnum = vo.getOP_CodeEnum();
				log.debug("[{}] Receive [{}] message from Client.", key, op_CodeEnum.name());
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
		runFlag = false;
	}
}
