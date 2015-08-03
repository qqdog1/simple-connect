package name.qd.simpleConnect.server.clientSocket;

import java.util.Hashtable;
import java.util.Map;

import name.qd.simpleConnect.common.constant.LogConstant;
import name.qd.simpleConnect.server.ServerConfigLoader;

import org.apache.log4j.Logger;

public class LoginKeyControl {
	private static LoginKeyControl instance = new LoginKeyControl();
	
	private Logger mLogger = Logger.getLogger(LogConstant.SERVER_LOG);
	
	private Map<String, byte[]> map = new Hashtable<String, byte[]>();
	private static final String KEY_SPLIT = ":";
	
	private boolean isAllowSameIpLogin;
	private int iAllowSameIpCount;
	
	private LoginKeyControl() {
		isAllowSameIpLogin = ServerConfigLoader.getInstance().isAllowSameIpLogin();
		iAllowSameIpCount = ServerConfigLoader.getInstance().getAllSameIpCount();
	}
	
	public static LoginKeyControl getInstance() {
		return instance;
	}
	
	public String getNewLoginKey(String sIp) {
		if(isAllowSameIpLogin) {
			return getNewLoginKeyAllowSameIp(sIp);
		} else {
			return getNewLoginKeyNotAllowSameIp(sIp);
		}
	}
	
	private String getNewLoginKeyAllowSameIp(String sKey) {
		if(map.containsKey(sKey)) {
			byte[] b = map.get(sKey);
			for(int i = 0 ; i < iAllowSameIpCount ; i++) {
				if(b[i] != 1) {
					b[i] = 1;
					map.put(sKey, b);
					return sKey + KEY_SPLIT + i;
				}
			}
			return null;
		} else {
			byte[] b = new byte[iAllowSameIpCount];
			b[0] = 1;
			map.put(sKey, b);
			return sKey + KEY_SPLIT + 0;
		}
	}
	
	private String getNewLoginKeyNotAllowSameIp(String sKey) {
		if(!map.containsKey(sKey)) {
			byte[] b = new byte[0];
			map.put(sKey, b);
			return sKey;
		}
		return null;
	}
	
	public boolean removeLoginKey(String sKey) {
		if(isAllowSameIpLogin) {
			String[] s = sKey.split(KEY_SPLIT);
			if(s.length != 2) {
				mLogger.error("Wrong Key Pattern. Key:[" + sKey + "]");
				return false;
			}
			if(map.containsKey(s[0])) {
				int i = Integer.parseInt(s[1]);
				byte[] b = map.get(s[0]);
				b[i] = 0;
				return true;
			} else {
				mLogger.error("Remove Login Key failed. Can't Identify Key:[" + sKey + "]");
				return false;
			}
		} else {
			if(map.containsKey(sKey)) {
				map.remove(sKey);
				return true;
			} else {
				return false;
			}
		}
	}
}