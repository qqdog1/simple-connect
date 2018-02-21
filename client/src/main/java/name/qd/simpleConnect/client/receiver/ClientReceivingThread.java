package name.qd.simpleConnect.client.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.client.ClientThread;
import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;

public class ClientReceivingThread extends Thread {
	private ClientThread clientThread;
	private ReceivingQManager receivingQManager;
	private boolean runFlag;
	
	private Logger log = LoggerFactory.getLogger(ClientReceivingThread.class);
	
	public ClientReceivingThread(ClientThread clientThread, ReceivingQManager receivingQManager) {
		this.clientThread = clientThread;
		this.receivingQManager = receivingQManager;
		runFlag = true;
		this.start();
	}
	
	public void run() {
		while(runFlag) {
			try {
				PackVo vo = receivingQManager.poll();
				
				if(vo == null) {
					if(runFlag) {
						clientThread.heartbeatTimeout();
					}
					continue;
				}
				
				OP_CodeEnum op_CodeEnum = vo.getOP_CodeEnum();
				log.debug("Receive [{}] message from Server.", op_CodeEnum.name());
				
				switch(op_CodeEnum) {
				case CONFIRM:
					clientThread.receiveConfirm();
					break;
				case DATA:
					clientThread.receiveData(vo.getData());
					break;
				case HEARTBEAT:
					break;
				case REJECT:
					clientThread.receiveReject(vo.getData());
					break;
				default:
					break;
				}
			} catch (InterruptedException e) {
				log.error("ClientReceivingThread run failed.", e);
			}
		}
	}
	
	public void closeReceivingThread() {
		runFlag = false;
	}
}
