package name.qd.simpleConnect.server.clientSocket;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.qd.simpleConnect.server.ServerConfigLoader;

public class LoginKeyControl {
	private static LoginKeyControl instance = new LoginKeyControl();
	
	private Logger log = LoggerFactory.getLogger(LoginKeyControl.class);
	
	private Map<String, byte[]> map = new Hashtable<String, byte[]>();
	private static final String KEY_SPLIT = ":";
	
	private boolean isAllowSameIpLogin;
	private int allowSameIpCount;
	
	private LoginKeyControl() {
		isAllowSameIpLogin = ServerConfigLoader.getInstance().isAllowSameIpLogin();
		allowSameIpCount = ServerConfigLoader.getInstance().getAllSameIpCount();
	}
	
	public static LoginKeyControl getInstance() {
		return instance;
	}
	
	public String getNewLoginKey(String ip) {
		if(isAllowSameIpLogin) {
			return getNewLoginKeyAllowSameIp(ip);
		} else {
			return getNewLoginKeyNotAllowSameIp(ip);
		}
	}
	
	private String getNewLoginKeyAllowSameIp(String key) {
		if(map.containsKey(key)) {
			byte[] b = map.get(key);
			for(int i = 0 ; i < allowSameIpCount ; i++) {
				if(b[i] != 1) {
					b[i] = 1;
					map.put(key, b);
					return key + KEY_SPLIT + i;
				}
			}
			return null;
		} else {
			byte[] b = new byte[allowSameIpCount];
			b[0] = 1;
			map.put(key, b);
			return key + KEY_SPLIT + 0;
		}
	}
	
	private String getNewLoginKeyNotAllowSameIp(String key) {
		if(!map.containsKey(key)) {
			byte[] b = new byte[0];
			map.put(key, b);
			return key;
		}
		return null;
	}
	
	public boolean removeLoginKey(String key) {
		if(isAllowSameIpLogin) {
			String[] s = key.split(KEY_SPLIT);
			if(s.length != 2) {
				log.error("Wrong Key Pattern. Key:[{}]", key);
				return false;
			}
			if(map.containsKey(s[0])) {
				int i = Integer.parseInt(s[1]);
				byte[] b = map.get(s[0]);
				b[i] = 0;
				return true;
			} else {
				log.error("Remove Login Key failed. Can't Identify Key:[{}]", key);
				return false;
			}
		} else {
			if(map.containsKey(key)) {
				map.remove(key);
				return true;
			} else {
				return false;
			}
		}
	}
}