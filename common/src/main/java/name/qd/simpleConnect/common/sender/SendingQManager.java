package name.qd.simpleConnect.common.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;

import org.apache.log4j.Logger;

public class SendingQManager extends Thread {
	private Logger mLogger;
	
	private String sSenderName;
	
	private ArrayBlockingQueue<byte[]> queue;
	private int iQueueSize;
	
	private int iHeartbeatInterval;
	
	private OutputStream outputStream;
	
	private boolean bRunFlag;
	
	public SendingQManager(Logger logger, int iQueueSize, int iHeartbeatInterval, OutputStream outputStream, String sSenderName) {
		this.mLogger = logger;
		this.iQueueSize = iQueueSize;
		this.iHeartbeatInterval = iHeartbeatInterval;
		this.outputStream = outputStream;
		this.sSenderName = sSenderName;
		
		startSendingQueue();
	}
	
	private void startSendingQueue() {
		queue = new ArrayBlockingQueue<byte[]>(iQueueSize);
		
		mLogger.debug("[" + sSenderName + "], New Sending Queue size = [" + iQueueSize + "], start Sending Queue Thread.");
		
		bRunFlag = true;
		
		this.start();
	}
	
	public boolean putQueue(byte[] bData) {
		boolean bSuccess = queue.offer(SimpleConnectDataPacker.packingData(bData));
		if(bSuccess) {
			mLogger.debug("[" + sSenderName + "], Put Msg to Queue without Block. Data:[" + new String(bData) + "]");
		} else {
			mLogger.debug("[" + sSenderName + "], Not enough capacity in Sending Queue. Data:[" + new String(bData) + "]");
		}
		return bSuccess;
	}
	
	public boolean putQueue(byte[] bData, long lTimeout) {
		boolean bSuccess = false;
		try {
			bSuccess = queue.offer(SimpleConnectDataPacker.packingData(bData), lTimeout, TimeUnit.MILLISECONDS);
			if(bSuccess) {
				mLogger.debug("[" + sSenderName + "], Put Msg to Queue with Block. Data:[" + new String(bData) + "]");
			} else {
				mLogger.debug("[" + sSenderName + "], Not enough capacity in Sending Queue. Timeout:[" + lTimeout + "], Data:[" + new String(bData) + "]");
			}
		} catch (InterruptedException e) {
			mLogger.error(e);
		}
		return bSuccess;
	}
	
	public void run() {
		while(bRunFlag) {
			try {
				byte[] bData = queue.poll(iHeartbeatInterval, TimeUnit.MILLISECONDS);
				
				if(bData != null) {
					send(bData);
				} else {
					sendHeartbeat();
				}
			} catch (InterruptedException e) {
				mLogger.error(e);
			}
		}
	}
	
	private void sendHeartbeat() {
		send(SimpleConnectDataPacker.packingHeartbeat());
	}
	
	private void send(byte[] bData) {
		try {
			outputStream.write(bData);
			mLogger.debug("[" + sSenderName + "], Send Message. Data:[" + new String(bData) + "]");
		} catch (IOException e) {
			if(bRunFlag) {
				mLogger.error("[" + sSenderName + "], Send data failed. Data:[" + new String(bData) + "]", e);
			}
		}
	}
	
	public void closeSendingQThread() {
		bRunFlag = false;
	}
}
