import java.io.IOException;

import com.chulung.forwarder.wrapper.DataWrapper;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Test {

	public static void main(String[] args) throws IOException {
		// KryoPool kryoPool = KryoPool.getInstance();
		//
		// ByteBuf data = Unpooled.buffer();
		// kryoPool.encode(data, new DataWrapper());
		// kryoPool.decode(data);
		Test test = new Test();
		byte[] bytes = test.serialize(new DataWrapper());
		test.deserialize(bytes);
	}

	public byte[] serialize(Object t) {
		byte[] buffer = new byte[2048];
		Output output = new Output(buffer);
		Kryo kryo = new Kryo();
		kryo.writeClassAndObject(output, t);
		return output.toBytes();
	}

	public <T> T deserialize(byte[] bytes) {
		Kryo kryo = new Kryo();
		Input input = new Input(bytes);
		@SuppressWarnings("unchecked")
		T t = (T) kryo.readClassAndObject(input);
		return t;
	}

}
