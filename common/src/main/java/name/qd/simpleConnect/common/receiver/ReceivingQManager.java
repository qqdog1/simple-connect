package name.qd.simpleConnect.common.receiver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.packer.vo.PackVo;

public class ReceivingQManager {
	private ArrayBlockingQueue<PackVo> queue;
	private long timeOut;
	
	public ReceivingQManager(int queueSize, int heartbeatInterval, int heartbeatCount) {
		queue = new ArrayBlockingQueue<PackVo>(queueSize);
		timeOut = (long)heartbeatInterval * (long)heartbeatCount;
	}
	
	public void add(PackVo vo) throws IllegalStateException {
		queue.add(vo);
	}
	
	public PackVo poll() throws InterruptedException {
		return queue.poll(timeOut, TimeUnit.MILLISECONDS);
	}
}
