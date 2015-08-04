package name.qd.simpleConnect.client.receiver;

import org.apache.log4j.Logger;

import name.qd.simpleConnect.client.Client;
import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.common.receiver.ReceivingQManager;

public class ClientReceivingThread extends Thread {
	
	private Client client;
	private ReceivingQManager receivingQManager;
	private boolean bRunFlag;
	
	private Logger mLogger = Logger.getLogger(LogConstant.CLIENT_LOG);
	
	public ClientReceivingThread(Client client, ReceivingQManager receivingQManager) {
		this.client = client;
		this.receivingQManager = receivingQManager;
		bRunFlag = true;
		this.start();
	}
	
	public void run() {
		while(bRunFlag) {
			try {
				PackVo vo = receivingQManager.poll();
				
				if(vo == null) {
					if(bRunFlag) {
						client.heartbeatTimeout();
					}
					continue;
				}
				
				OP_CodeEnum op_CodeEnum = vo.getOP_CodeEnum();
				mLogger.debug("Receive [" + op_CodeEnum + "] message from Server.");
				
				switch(op_CodeEnum) {
				case CONFIRM:
					client.receiveConfirm();
					break;
				case DATA:
					client.receiveData(vo.getData());
					break;
				case HEARTBEAT:
					break;
				case REJECT:
					client.receiveReject(vo.getData());
					break;
				default:
					break;
				}
			} catch (InterruptedException e) {
				mLogger.error(e);
			}
		}
	}
	
	public void closeReceivingThread() {
		bRunFlag = false;
	}
}