package name.qd.simpleConnect.common.packer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import name.qd.simpleConnect.common.enumeration.OP_CodeEnum;
import name.qd.simpleConnect.common.packer.vo.PackVo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class SimpleConnectDataPackerBenchmark {

	private static final int INPUT_STREAM_INIT_SIZE = 10;

	public static void main(String[] s) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(SimpleConnectDataPackerBenchmark.class.getSimpleName())
				.warmupIterations(5)
				.measurementIterations(5)
//				.threads(4)
				.timeUnit(TimeUnit.MICROSECONDS)
//				.mode(Mode.AverageTime)
//				.mode(Mode.SingleShotTime)
				.forks(1)
				.build();
		new Runner(opt).run();
	}

	private PackVo vo;
	private InputStream inputStream;

	@Setup(Level.Trial)
	public void init() {
		// init Vo
		vo = new PackVo();
		vo.setData("QQKK123".getBytes());
		vo.setOP_CodeEnum(OP_CodeEnum.DATA);

		// init InputStream
		try {
			File file = new File("PackVoByte");
			FileOutputStream fOut = new FileOutputStream(file);
			file.createNewFile();
			for (int i = 0; i < INPUT_STREAM_INIT_SIZE; i++) {
				fOut.write(SimpleConnectDataPacker.packingDataUseByteBuffer(vo));
				fOut.flush();
			}
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			File file = new File("PackVoByte");
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@TearDown
	public void release() {
		try {
			inputStream.close();
			Files.deleteIfExists(Paths.get("PackVoByte"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	@BenchmarkMode(Mode.SingleShotTime)
	public void unpack() {
		try {
			SimpleConnectDataPacker.unpackingData(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
