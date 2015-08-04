package name.qd.simpleConnect.server;

import name.qd.simpleConnect.server.Server;

public class TestServer {
	Server server;
	
	public static void main(String[] s) {
		new TestServer();
	}
	
	private TestServer() {
		server = new Server("./config/ServerConfig.txt", new ServerReceiver(this));
		server.startServer();
	}
	
	public void send(String sClient, byte[] bData) {
		server.send(sClient, bData);
	}
}
