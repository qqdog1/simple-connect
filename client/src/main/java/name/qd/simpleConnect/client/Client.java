package name.qd.simpleConnect.client;

import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;

public class Client {
	
	private ClientThread clientThread;
	
	public Client(String sConfigPath, SimpleConnectReceiver receiver, String sClientName) {
		clientThread = new ClientThread(sConfigPath, receiver, sClientName);
	}
	
	public void connectToServer() {
		clientThread.connectToServer();
	}
	
	public boolean send(byte[] bData) {
		return clientThread.send(bData);
	}
	
	public boolean isConnect() {
		return clientThread.isConnect();
	}
	
	public void disconnect() {
		clientThread.disconnect();
	}
}