package name.qd.simpleConnect.client;

import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;

public class ClientReceiver implements SimpleConnectReceiver {

	private TestClient testClient;
	private int iReceiveCount;
	
	public ClientReceiver(TestClient testClient) {
		this.testClient = testClient;
	}
	
	public void onMessage(String sSessionId, byte[] bData) {
		System.out.println("R");
	}

}
