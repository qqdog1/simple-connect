package name.qd.simpleConnect.server.clientSocket;

import java.util.Hashtable;
import java.util.Map;

import name.qd.simpleConnect.common.constant.LogConstant;

import org.apache.log4j.Logger;

public class ClientSocketThreadManager {
	
	private Logger mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
	
	private Map<String, ClientSocketThread> map = new Hashtable<String, ClientSocketThread>();

	private static ClientSocketThreadManager instance = new ClientSocketThreadManager();
	
	public static ClientSocketThreadManager getInstance() {
		return instance;
	}
	
	private ClientSocketThreadManager() {
	}
	
	public void add(String sKey, ClientSocketThread clientSocketThread) {
		map.put(sKey, clientSocketThread);
		mLogger.debug("Add new ClientSocketThread, Key:[" + sKey + "]");
	}
	
	public void remove(String sKey) {
		if(map.containsKey(sKey)) {
			map.remove(sKey);
			mLogger.debug("Remove ClientSocketThread, Key:[" + sKey + "]");
		}
	}
	
	public boolean send(String sKey, byte[] bData) {
		if(containsKey(sKey)) {
			return map.get(sKey).send(bData);
		}
		return false;
	}
	
	public int send(byte[] bData) {
		int iCount = 0;
		synchronized(map) {
			for(ClientSocketThread clientSocketThread : map.values()) {
				if(clientSocketThread.send(bData)) {
					iCount++;
				}
			}
		}
		return iCount;
	}
	
	private boolean containsKey(String sKey) {
		return map.containsKey(sKey);
	}
}
