package name.qd.simpleConnect.common.enumeration;

import java.util.Arrays;

public enum OP_CodeEnum {
	CONFIRM("C".getBytes()),
	DATA("D".getBytes()),
	HEARTBEAT("H".getBytes()),
	REJECT("R".getBytes()),
	;
	
	private byte[] OP_Code;
	
	OP_CodeEnum(byte[] OP_Code) {
		this.OP_Code= OP_Code;
	}
	
	public byte[] getByteArray() {
		return OP_Code;
	}
	
	public static OP_CodeEnum getOP_CodeEnum(byte[] OP_Code) {
		for(OP_CodeEnum op_CodeEnum : OP_CodeEnum.values()) {
			if(Arrays.equals(OP_Code, op_CodeEnum.getByteArray())) {
				return op_CodeEnum;
			}
		}
		return null;
	}
}
