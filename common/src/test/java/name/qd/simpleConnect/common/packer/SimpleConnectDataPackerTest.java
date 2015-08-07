package name.qd.simpleConnect.common.packer;

import static org.junit.Assert.assertTrue;

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
		assertTrue(Arrays.equals(SimpleConnectDataPacker.packingData(vo), SimpleConnectDataPacker.packingDataUseByteBuffer(vo)));
	}
	
	@Test
	public void UnpackingTest() {
		
	}
}
