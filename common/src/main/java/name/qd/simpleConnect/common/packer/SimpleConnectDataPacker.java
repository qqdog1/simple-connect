package name.qd.simpleConnect.common.packer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import name.qd.simpleConnect.common.constant.PackerConstant;
import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;

public class SimpleConnectDataPacker {
	public static PackVo unpackingData(InputStream inputStream) throws IOException {
		PackVo vo = new PackVo();
		readAndCheckUnit(inputStream, PackerConstant.BOU);
		int length = readLength(inputStream);
		byte[] data = readBytesByLength(inputStream, length);
		if(readAndCheckUnit(inputStream, PackerConstant.EOU)) {
			byte[] op_Code = Arrays.copyOfRange(data, 0, PackerConstant.OP_LENGTH);
			vo.setOP_CodeEnum(OP_CodeEnum.getOP_CodeEnum(op_Code));
			vo.setData(Arrays.copyOfRange(data, PackerConstant.OP_LENGTH, data.length));
		} else {
			return unpackingData(inputStream);
		}
		return vo;
	}
	
	public static byte[] packingData(PackVo vo) {
		int totalLength = PackerConstant.BOU_EOU_LENGTH + PackerConstant.LENGTH_LENGTH + PackerConstant.OP_LENGTH + PackerConstant.BOU_EOU_LENGTH;
		totalLength += vo.getData().length;
		byte[] data = new byte[totalLength];
		int offset = 0;
		data = writeBOU(data, offset);
		offset += PackerConstant.BOU_EOU_LENGTH;
		data = writeLength(data, vo, offset);
		offset += PackerConstant.LENGTH_LENGTH;
		data = writePackVo(data, vo, offset);
		offset += PackerConstant.OP_LENGTH + vo.getData().length;
		data = writeEOU(data, offset);
		return data;
	}
	
	public static byte[] packingDataUseByteBuffer(PackVo vo) {
		int totalLength = PackerConstant.BOU_EOU_LENGTH + PackerConstant.LENGTH_LENGTH + PackerConstant.OP_LENGTH + PackerConstant.BOU_EOU_LENGTH;
		totalLength += vo.getData().length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);
		byteBuffer.put(PackerConstant.BOU);
		byteBuffer.put(PackerConstant.BOU);
		byteBuffer.put((byte)((PackerConstant.OP_LENGTH + vo.getData().length) / PackerConstant.POSITIVE_BYTE_SIZE));
		byteBuffer.put((byte)((PackerConstant.OP_LENGTH + vo.getData().length) % PackerConstant.POSITIVE_BYTE_SIZE));
		byteBuffer.put(vo.getOP_CodeEnum().getByteArray());
		byteBuffer.put(vo.getData());
		byteBuffer.put(PackerConstant.EOU);
		byteBuffer.put(PackerConstant.EOU);
		return byteBuffer.array();
	}
	
	public static byte[] packingData(byte[] data) {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
		vo.setData(data);
		
		return packingData(vo);
	}
	
	public static byte[] packingHeartbeat() {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.HEARTBEAT);
		
		return packingData(vo);
	}
	
	private static boolean readAndCheckUnit(InputStream inputStream, byte unit) throws IOException {
		int readLength = 0;
		while(readLength != PackerConstant.BOU_EOU_LENGTH) {
			byte[] b = readBytesByLength(inputStream, 1);
			if(b != null && b[0] == unit) {
				readLength++;
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static int readLength(InputStream inputStream) throws IOException {
		byte[] data = readBytesByLength(inputStream, PackerConstant.LENGTH_LENGTH);
		int length = 0;
		for(int i = 0 ; i < data.length ; i++) {
			length *= PackerConstant.POSITIVE_BYTE_SIZE;
			length += Integer.valueOf(data[i]);
		}
		return length;
	}
	
	private static byte[] readBytesByLength(InputStream inputStream, int length) throws IOException {
		byte[] data = new byte[length];
		int readLength = 0;
		int readTotalLength = 0;
		
		while(readTotalLength < length) {
			readLength = inputStream.read(data, readLength, length - readLength);
			
			if(readLength == -1) {
				throw new IOException("Read byte failed. Socket close.");
			} else {
				readTotalLength += readLength;
			}
		}
		return data;
	}
	
	private static byte[] writeBOU(byte[] data, int offset) {
		for(int i = 0 ; i < PackerConstant.BOU_EOU_LENGTH ; i++) {
			data[offset+i] = PackerConstant.BOU;
		}
		return data;
	}
	
	private static byte[] writeEOU(byte[] data, int offset) {
		for(int i = 0 ; i < PackerConstant.BOU_EOU_LENGTH ; i++) {
			data[offset+i] = PackerConstant.EOU;
		}
		return data;
	}
	
	private static byte[] writeLength(byte[] data, PackVo vo, int offset) {
		int length = PackerConstant.OP_LENGTH + vo.getData().length;
		byte[] bLength = new byte[PackerConstant.LENGTH_LENGTH];
		bLength[0] = (byte)(length / PackerConstant.POSITIVE_BYTE_SIZE);
		bLength[1] = (byte)(length % PackerConstant.POSITIVE_BYTE_SIZE);
		System.arraycopy(bLength, 0, data, offset, PackerConstant.LENGTH_LENGTH);
		return data;
	}
	
	private static byte[] writePackVo(byte[] data, PackVo vo, int offset) {
		data[offset] = vo.getOP_CodeEnum().getByteArray()[0];
		int iDataLength = vo.getData().length;
		System.arraycopy(vo.getData(), 0, data, offset+PackerConstant.OP_LENGTH, iDataLength);
		return data;
	}
}
