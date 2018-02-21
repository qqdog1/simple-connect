package name.qd.simpleConnect.server.clientSocket;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSocketThreadManager {
	private Logger log = LoggerFactory.getLogger(ClientSocketThreadManager.class);
	
	private Map<String, ClientSocketThread> map = new Hashtable<String, ClientSocketThread>();

	private static ClientSocketThreadManager instance = new ClientSocketThreadManager();
	
	public static ClientSocketThreadManager getInstance() {
		return instance;
	}
	
	private ClientSocketThreadManager() {
	}
	
	public void add(String key, ClientSocketThread clientSocketThread) {
		map.put(key, clientSocketThread);
		log.debug("Add new ClientSocketThread, Key:[{}]", key);
	}
	
	public void remove(String key) {
		if(map.containsKey(key)) {
			map.remove(key);
			log.debug("Remove ClientSocketThread, Key:[{}]", key);
		}
	}
	
	public boolean send(String key, byte[] data) {
		if(containsKey(key)) {
			return map.get(key).send(data);
		}
		return false;
	}
	
	public int send(byte[] data) {
		int count = 0;
		synchronized(map) {
			for(ClientSocketThread clientSocketThread : map.values()) {
				if(clientSocketThread.send(data)) {
					count++;
				}
			}
		}
		return count;
	}
	
	private boolean containsKey(String key) {
		return map.containsKey(key);
	}
}
