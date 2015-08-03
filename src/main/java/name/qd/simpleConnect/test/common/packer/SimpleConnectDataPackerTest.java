package name.qd.simpleConnect.test.common.packer;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;

import org.junit.Test;

public class SimpleConnectDataPackerTest {
	
	@Test
	public void PackingTest() {
		int iCount = 30000;
		
		PackVo vo = new PackVo();
		vo.setData("QQKK123".getBytes());
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
		
		for(int i = 0 ; i < iCount ; i++) {
			SimpleConnectDataPacker.packingData(vo);
		}
		
		for(int i = 0 ; i < iCount ; i++) {
			SimpleConnectDataPacker.packingDataUseByteBuffer(vo);
		}
		
		System.out.println(new String(SimpleConnectDataPacker.packingData(vo)));
		System.out.println(new String(SimpleConnectDataPacker.packingDataUseByteBuffer(vo)));
		
		// --------------
		long lTime = System.nanoTime();
		
		for(int i = 0 ; i < iCount ; i++) {
			//SimpleConnectDataPacker.packingData(vo);
			SimpleConnectDataPacker.packingDataUseByteBuffer(vo);
		}
		
		System.out.println(System.nanoTime() - lTime);
		
		//
		
		lTime = System.nanoTime();
		
		for(int i = 0 ; i < iCount ; i++) {
			//SimpleConnectDataPacker.packingDataUseByteBuffer(vo);
			SimpleConnectDataPacker.packingData(vo);
		}
		
		System.out.println(System.nanoTime() - lTime);
	}
	
	@Test
	public void UnpackingTest() {
		
	}
}
