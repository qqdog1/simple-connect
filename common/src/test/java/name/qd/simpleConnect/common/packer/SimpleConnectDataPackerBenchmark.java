package name.qd.simpleConnect.common.packer;

import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class SimpleConnectDataPackerBenchmark {

	public static void main(String[] s) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(SimpleConnectDataPackerBenchmark.class.getSimpleName())
				.warmupIterations(5)
                .measurementIterations(5)
//                .threads(4)
				.timeUnit(TimeUnit.MICROSECONDS)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
	
	private PackVo vo;
	
	@Setup(Level.Trial)
    public void setUp() {
        vo = new PackVo();
		vo.setData("QQKK123".getBytes());
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);
    }
	
	@Benchmark
	public void newVo() {
		PackVo testVo = new PackVo();
		testVo.setData("QQKK123".getBytes());
		testVo.setOP_CodeEnum(OP_CodeEnum.DATA);
	}
	
	@Benchmark
	public void packing() {
		SimpleConnectDataPacker.packingData(vo);
	}
	
	@Benchmark
	public void packingUseByteBuffer() {
		SimpleConnectDataPacker.packingDataUseByteBuffer(vo);
	}
	
	@Benchmark
	public void unpack() {
		
	}
}
