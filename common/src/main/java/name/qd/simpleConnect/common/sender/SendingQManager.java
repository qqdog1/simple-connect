package name.qd.simpleConnect.common.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendingQManager extends Thread {
	private Logger log = LoggerFactory.getLogger(SendingQManager.class);
	
	private String senderName;
	
	private ArrayBlockingQueue<byte[]> queue;
	private int queueSize;
	
	private int heartbeatInterval;
	
	private OutputStream outputStream;
	
	private boolean runFlag;
	
	public SendingQManager(int queueSize, int heartbeatInterval, OutputStream outputStream, String senderName) {
		this.queueSize = queueSize;
		this.heartbeatInterval = heartbeatInterval;
		this.outputStream = outputStream;
		this.senderName = senderName;
		
		startSendingQueue();
	}
	
	private void startSendingQueue() {
		queue = new ArrayBlockingQueue<byte[]>(queueSize);
		
		log.debug("[{}], New Sending Queue size = [{}], start Sending Queue Thread.", senderName, queueSize);
		
		runFlag = true;
		
		this.start();
	}
	
	public boolean putQueue(byte[] data) {
		boolean success = queue.offer(SimpleConnectDataPacker.packingData(data));
		if(success) {
			log.debug("[{}], Put Msg to Queue without Block. Data:[{}]", senderName, new String(data));
		} else {
			log.debug("[{}], Not enough capacity in Sending Queue. Data:[{}]", senderName, new String(data));
		}
		return success;
	}
	
	public boolean putQueue(byte[] data, long timeout) {
		boolean success = false;
		try {
			success = queue.offer(SimpleConnectDataPacker.packingData(data), timeout, TimeUnit.MILLISECONDS);
			if(success) {
				log.debug("[{}], Put Msg to Queue with Block. Data:[{}]", senderName, new String(data));
			} else {
				log.debug("[{}], Not enough capacity in Sending Queue. Timeout:[{}], Data:[{}]", senderName, timeout, new String(data));
			}
		} catch (InterruptedException e) {
			log.error("put queue failed.", e);
		}
		return success;
	}
	
	public void run() {
		while(runFlag) {
			try {
				byte[] data = queue.poll(heartbeatInterval, TimeUnit.MILLISECONDS);
				
				if(data != null) {
					send(data);
				} else {
					sendHeartbeat();
				}
			} catch (InterruptedException e) {
				log.error("Sending Queue run failed.", e);
			}
		}
	}
	
	private void sendHeartbeat() {
		send(SimpleConnectDataPacker.packingHeartbeat());
	}
	
	private void send(byte[] data) {
		try {
			outputStream.write(data);
			log.debug("[{}], Send Message. Data:[{}]", senderName, new String(data));
		} catch (IOException e) {
			if(runFlag) {
				log.error("[{}], Send data failed. Data:[{}]", senderName, new String(data), e);
			}
		}
	}
	
	public void closeSendingQThread() {
		runFlag = false;
	}
}
