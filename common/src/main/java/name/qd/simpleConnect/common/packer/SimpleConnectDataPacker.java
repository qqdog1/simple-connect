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
		readAndCheckBOU(inputStream);
		int iLength = readLength(inputStream);
		byte[] bData = readBytesByLength(inputStream, iLength);
		if(readAndCheckEOU(inputStream)) {
			byte[] bOP_Code = Arrays.copyOfRange(bData, 0, PackerConstant.OP_LENGTH);
			vo.setOP_CodeEnum(OP_CodeEnum.getOP_CodeEnum(bOP_Code));
			vo.setData(Arrays.copyOfRange(bData, PackerConstant.OP_LENGTH, bData.length));
		} else {
			return unpackingData(inputStream);
		}
		return vo;
	}
	
	public static byte[] packingData(PackVo vo) {
		int iTotalLength = PackerConstant.BOU_EOU_LENGTH + PackerConstant.LENGTH_LENGTH + PackerConstant.OP_LENGTH + PackerConstant.BOU_EOU_LENGTH;
		iTotalLength += vo.getData().length;
		byte[] bData = new byte[iTotalLength];
		int iOffset = 0;
		bData = writeBOU(bData, iOffset);
		iOffset += PackerConstant.BOU_EOU_LENGTH;
		bData = writeLength(bData, vo, iOffset);
		iOffset += PackerConstant.LENGTH_LENGTH;
		bData = writePackVo(bData, vo, iOffset);
		iOffset += PackerConstant.OP_LENGTH + vo.getData().length;
		bData = writeEOU(bData, iOffset);
		return bData;
	}
	
	public static byte[] packingDataUseByteBuffer(PackVo vo) {
		int iTotalLength = PackerConstant.BOU_EOU_LENGTH + PackerConstant.LENGTH_LENGTH + PackerConstant.OP_LENGTH + PackerConstant.BOU_EOU_LENGTH;
		iTotalLength += vo.getData().length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(iTotalLength);
		byteBuffer.put(PackerConstant.BOU);
		byteBuffer.put(PackerConstant.BOU);
		byteBuffer.put((byte)(PackerConstant.OP_LENGTH + vo.getData().length / PackerConstant.POSITIVE_BYTE_SIZE));
		byteBuffer.put((byte)(PackerConstant.OP_LENGTH + vo.getData().length % PackerConstant.POSITIVE_BYTE_SIZE));
		byteBuffer.put(vo.getOP_CodeEnum().getByteArray());
		byteBuffer.put(vo.getData());
		byteBuffer.put(PackerConstant.EOU);
		byteBuffer.put(PackerConstant.EOU);
		
		return byteBuffer.array();
	}
	
	public static byte[] packingData(byte[] bData) {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
		vo.setData(bData);
		
		return packingData(vo);
	}
	
	public static byte[] packingHeartbeat() {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.HEARTBEAT);
		
		return packingData(vo);
	}
	
	private static void readAndCheckBOU(InputStream inputStream) throws IOException {
		int iReadLength = 0;
		while(iReadLength != PackerConstant.BOU_EOU_LENGTH) {
			byte[] b = readBytesByLength(inputStream, 1);
			if(b != null && b[0] == PackerConstant.BOU) {
				iReadLength++;
			} else {
				iReadLength = 0;
			}
		}
	}
	
	private static boolean readAndCheckEOU(InputStream inputStream) throws IOException {
		int iReadLength = 0;
		while(iReadLength != PackerConstant.BOU_EOU_LENGTH) {
			byte[] b = readBytesByLength(inputStream, 1);
			if(b != null && b[0] == PackerConstant.EOU) {
				iReadLength++;
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static int readLength(InputStream inputStream) throws IOException {
		byte[] b = readBytesByLength(inputStream, PackerConstant.LENGTH_LENGTH);
		int iLength = 0;
		for(int i = 0 ; i < b.length ; i++) {
			iLength *= PackerConstant.POSITIVE_BYTE_SIZE;
			iLength += Integer.valueOf(b[i]);
		}
		return iLength;
	}
	
	private static byte[] readBytesByLength(InputStream inputStream, int iLength) throws IOException {
		byte[] bData = new byte[iLength];
		int iReadLength = 0;
		int iReadTotalLength = 0;
		
		while(iReadTotalLength < iLength) {
			iReadLength = inputStream.read(bData, iReadLength, iLength - iReadLength);
			
			if(iReadLength == -1) {
				throw new IOException("Read byte failed. Socket close.");
			} else {
				iReadTotalLength += iReadLength;
			}
		}
		return bData;
	}
	
	private static byte[] writeBOU(byte[] bData, int iOffset) {
		for(int i = 0 ; i < PackerConstant.BOU_EOU_LENGTH ; i++) {
			bData[iOffset+i] = PackerConstant.BOU;
		}
		return bData;
	}
	
	private static byte[] writeEOU(byte[] bData, int iOffset) {
		for(int i = 0 ; i < PackerConstant.BOU_EOU_LENGTH ; i++) {
			bData[iOffset+i] = PackerConstant.EOU;
		}
		return bData;
	}
	
	private static byte[] writeLength(byte[] bData, PackVo vo, int iOffset) {
		int iLength = PackerConstant.OP_LENGTH + vo.getData().length;
		byte[] bLength = new byte[PackerConstant.LENGTH_LENGTH];
		bLength[0] = (byte)(iLength / PackerConstant.POSITIVE_BYTE_SIZE);
		bLength[1] = (byte)(iLength % PackerConstant.POSITIVE_BYTE_SIZE);
		System.arraycopy(bLength, 0, bData, iOffset, PackerConstant.LENGTH_LENGTH);
		return bData;
	}
	
	private static byte[] writePackVo(byte[] bData, PackVo vo, int iOffset) {
		bData[iOffset] = vo.getOP_CodeEnum().getByteArray()[0];
		int iDataLength = vo.getData().length;
		System.arraycopy(vo.getData(), 0, bData, iOffset+PackerConstant.OP_LENGTH, iDataLength);
		return bData;
	}
}
