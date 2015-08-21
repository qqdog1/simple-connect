package name.qd.simpleConnect.common.packer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;

import org.junit.Test;

public class SimpleConnectDataPackerTest {
	
	@Test
	public void PackingTest() {
		PackVo vo = new PackVo();
		vo.setData("QQKK123".getBytes());
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
		
		byte[] bPack = SimpleConnectDataPacker.packingData(vo);
		byte[] bPackBuf = SimpleConnectDataPacker.packingDataUseByteBuffer(vo);
		
		assertTrue(Arrays.equals(bPack, bPackBuf));
	}
	
	@Test
	public void UnpackingTest() {
		// init Vo
        PackVo vo = new PackVo();
		vo.setData("QQKK123".getBytes());
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
		
		// init InputStream
		try {
			File file = new File("PackVoByte");
			FileOutputStream fOut = new FileOutputStream(file);
			file.createNewFile();
			fOut.write(SimpleConnectDataPacker.packingDataUseByteBuffer(vo));
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream inputStream = null;
		try {
			File file = new File("PackVoByte");
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			PackVo vo2 = SimpleConnectDataPacker.unpackingData(inputStream);
			System.out.println(new String(vo.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
