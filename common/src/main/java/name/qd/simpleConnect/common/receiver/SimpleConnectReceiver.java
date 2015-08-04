package name.qd.simpleConnect.common.receiver;

public interface SimpleConnectReceiver {
	public void onMessage(String sSessionId, byte[] bData);
}
