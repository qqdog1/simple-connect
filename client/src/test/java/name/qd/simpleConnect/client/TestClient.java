package name.qd.simpleConnect.client;


public class TestClient {
	
	private static final int TOTAL_COUNT = 10;
	
	private byte[] bData = "abc".getBytes();
	
	private int iSuccessCount = 0;
	private int iFalseCount = 0;

	public static void main(String[] args) {
		new TestClient();
	}

	private TestClient() {
		Client client = new Client("./config/ClientConfig.txt", new ClientReceiver(this), "TestClient1");
		
		client.connectToServer();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//==============================================
		long lTime = System.nanoTime();
		
		for(int i = 0 ; i < TOTAL_COUNT ; i++) {
			if(client.send(bData)) {
				iSuccessCount++;
			} else {
				iFalseCount++;
			}
		}
		
		lTime = System.nanoTime() - lTime;
		System.out.println(lTime);
	}
}
