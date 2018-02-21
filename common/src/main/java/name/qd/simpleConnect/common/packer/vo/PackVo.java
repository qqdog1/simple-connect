package name.qd.simpleConnect.common.packer.vo;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;

public class PackVo {
	private OP_CodeEnum op_CodeEnum;
	private byte[] data = new byte[]{0};
	
	public OP_CodeEnum getOP_CodeEnum() {
		return op_CodeEnum;
	}
	public void setOP_CodeEnum(OP_CodeEnum op_CodeEnum) {
		this.op_CodeEnum = op_CodeEnum;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String toString() {
		return "OP_CodeEnum:[" + op_CodeEnum + "], Data:[" + new String(data) + "]";
	}
}