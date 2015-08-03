package name.qd.simpleConnect.test.client;
import name.qd.simpleConnect.client.Client;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class TestClient {
	
	private static final int TOTAL_COUNT = 10000;
	private Logger mLogger;
	
	private byte[] bData = "abc".getBytes();
	
	private int iSuccessCount = 0;
	private int iFalseCount = 0;

	public static void main(String[] args) {
		new TestClient();
	}

	private TestClient() {
		PropertyConfigurator.configure("./config/testLog4j.properties");
		mLogger = Logger.getLogger("client");
		
		Client client = new Client("./config/ClientConfig.txt", new ClientReceiver(this), "TestClient1");
		
		client.connectToServer();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//==============================================
		mLogger.info("Total Count:[" + TOTAL_COUNT + "]");
		long lTime = System.nanoTime();
		
		for(int i = 0 ; i < TOTAL_COUNT ; i++) {
			if(client.send(bData)) {
				iSuccessCount++;
			} else {
				iFalseCount++;
			}
		}
		
		lTime = System.nanoTime() - lTime;
		mLogger.info("Total Send Cost Time: " + lTime + " ns.");
		mLogger.info("Send Success Count: " + iSuccessCount + ".");
		mLogger.info("Send Failed Count: " + iFalseCount + ".");
	}
}
