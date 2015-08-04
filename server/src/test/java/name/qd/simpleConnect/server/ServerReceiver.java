package name.qd.simpleConnect.server;

import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;

public class ServerReceiver implements SimpleConnectReceiver {

	private TestServer testServer;
	
	public ServerReceiver(TestServer testServer) {
		this.testServer = testServer;
	}
	
	public void onMessage(String sSessionId, byte[] bData) {
//		for(int i = 0 ; i < 10 ; i++) {
			testServer.send(sSessionId, bData);
//		}
//		System.out.println("Send Over.");
	}
}
