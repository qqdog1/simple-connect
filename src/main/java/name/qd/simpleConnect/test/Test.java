package name.qd.simpleConnect.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.SimpleConnectDataPacker;
import name.qd.simpleConnect.common.packer.vo.PackVo;
import name.qd.simpleConnect.server.ServerConfigLoader;
import name.qd.simpleConnect.server.clientSocket.LoginKeyControl;




public class Test {
	
	public static void main(String[] s) {
		new Test();
	}
	
	private Test() {
		packUnPackTest();
	}
	
	private void enumTest() {
		byte[] b = "C".getBytes();
		
		OP_CodeEnum op = OP_CodeEnum.getOP_CodeEnum(b);
		
		switch(op) {
		case CONFIRM:
			System.out.println(op);
			break;
		case DATA:
			break;
		case HEARTBEAT:
			break;
		case REJECT:
			break;
		default:
			break;
		}
	}
	
	private void packUnPackTest() {
		PackVo vo = new PackVo();
		vo.setOP_CodeEnum(OP_CodeEnum.REJECT);
		vo.setData(new byte[]{'A'});
		byte[] b = SimpleConnectDataPacker.packingData(vo);
		
		ByteArrayInputStream bIn = new ByteArrayInputStream(b);
		try {
			PackVo vo2 = SimpleConnectDataPacker.unpackingData(bIn);
			System.out.println(vo2.getOP_CodeEnum());
			System.out.println(new String(vo2.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loginIdTest() {
		try {
			ServerConfigLoader s = ServerConfigLoader.getInstance();
			ServerConfigLoader.getInstance().init("./config/ServerConfig.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0 ; i < 6 ; i++) {
			System.out.println(LoginKeyControl.getInstance().getNewLoginKey("QQ"));
		}
		
		LoginKeyControl.getInstance().removeLoginKey("QQ:3");
		
		for(int i = 0 ; i < 6 ; i++) {
			System.out.println(LoginKeyControl.getInstance().getNewLoginKey("QQ"));
		}
		
		LoginKeyControl.getInstance().removeLoginKey("QQ:0");
		System.out.println(LoginKeyControl.getInstance().getNewLoginKey("QQ"));
	}
}

