package name.qd.simpleConnect.common.enumeration;

import java.util.Arrays;

public enum OP_CodeEnum {
	CONFIRM("C".getBytes()),
	DATA("D".getBytes()),
	HEARTBEAT("H".getBytes()),
	REJECT("R".getBytes()),
	;
	
	private byte[] bOP_Code;
	
	OP_CodeEnum(byte[] bOP_Code) {
		this.bOP_Code= bOP_Code;
	}
	
	public byte[] getByteArray() {
		return bOP_Code;
	}
	
	public static OP_CodeEnum getOP_CodeEnum(byte[] bOP_Code) {
		for(OP_CodeEnum op_CodeEnum : OP_CodeEnum.values()) {
			if(Arrays.equals(bOP_Code, op_CodeEnum.getByteArray())) {
				return op_CodeEnum;
			}
		}
		return null;
	}
}
