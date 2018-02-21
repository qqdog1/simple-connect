package name.qd.simpleConnect.client;

import name.qd.simpleConnect.common.receiver.SimpleConnectReceiver;

public class Client {
	
	private ClientThread clientThread;
	
	public Client(String configPath, SimpleConnectReceiver receiver, String clientName) {
		clientThread = new ClientThread(configPath, receiver, clientName);
	}
	
	public void connectToServer() {
		clientThread.connectToServer();
	}
	
	public boolean send(byte[] data) {
		return clientThread.send(data);
	}
	
	public boolean isConnect() {
		return clientThread.isConnect();
	}
	
	public void disconnect() {
		clientThread.disconnect();
	}
}