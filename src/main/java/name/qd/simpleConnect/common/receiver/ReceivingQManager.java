package name.qd.simpleConnect.common.receiver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.packer.vo.PackVo;

public class ReceivingQManager {
	private ArrayBlockingQueue<PackVo> queue;
	private long lTimeOut;
	
	public ReceivingQManager(int iQueueSize, int iHeartbeatInterval, int iHeartbeatCount) {
		queue = new ArrayBlockingQueue<PackVo>(iQueueSize);
		lTimeOut = (long)iHeartbeatInterval * (long)iHeartbeatCount;
	}
	
	public void add(PackVo vo) throws IllegalStateException {
		queue.add(vo);
	}
	
	public PackVo poll() throws InterruptedException {
		return queue.poll(lTimeOut, TimeUnit.MILLISECONDS);
	}
}
