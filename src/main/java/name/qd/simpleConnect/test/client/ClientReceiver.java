package name.qd.simpleConnect.test.client;

import org.apache.log4j.Logger;

import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;

public class ClientReceiver implements SimpleConnectReceiver {

	private TestClient testClient;
	private int iReceiveCount;
	
	private Logger mLogger = Logger.getLogger("client");
	
	public ClientReceiver(TestClient testClient) {
		this.testClient = testClient;
	}
	
	public void onMessage(String sSessionId, byte[] bData) {
		mLogger.info("Receive " + ++iReceiveCount);
	}

}
