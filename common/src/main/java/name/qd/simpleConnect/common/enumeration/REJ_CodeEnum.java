package name.qd.simpleConnect.common.enumeration;

import java.util.Arrays;

public enum REJ_CodeEnum {
	SAME_IP_FULL("1".getBytes()),
	;
	
	private byte[] bRej_Code;
	
	REJ_CodeEnum(byte[] bRej_Code) {
		this.bRej_Code = bRej_Code;
	}
	
	public byte[] getByteArray() {
		return bRej_Code;
	}
	
	public static REJ_CodeEnum getREJ_CodeEnum(byte[] bRej_Code) {
		for(REJ_CodeEnum rej_CodeEnum : REJ_CodeEnum.values()) {
			if(Arrays.equals(bRej_Code, rej_CodeEnum.getByteArray())) {
				return rej_CodeEnum;
			}
		}
		return null;
	}
}
