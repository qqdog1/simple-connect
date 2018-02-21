package name.qd.simpleConnect.common.enumeration;

import java.util.Arrays;

public enum REJ_CodeEnum {
	SAME_IP_FULL("1".getBytes()),
	;
	
	private byte[] rej_Code;
	
	REJ_CodeEnum(byte[] rej_Code) {
		this.rej_Code = rej_Code;
	}
	
	public byte[] getByteArray() {
		return rej_Code;
	}
	
	public static REJ_CodeEnum getREJ_CodeEnum(byte[] rej_Code) {
		for(REJ_CodeEnum rej_CodeEnum : REJ_CodeEnum.values()) {
			if(Arrays.equals(rej_Code, rej_CodeEnum.getByteArray())) {
				return rej_CodeEnum;
			}
		}
		return null;
	}
}
