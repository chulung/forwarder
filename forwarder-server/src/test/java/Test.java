import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Test {
	public static void main(String[] args) throws IOException {
		new Thread(() -> {
			try {
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.bind(new InetSocketAddress(7777));
				SocketChannel channel = serverSocketChannel.accept();
				ByteBuffer dst = ByteBuffer.allocate(20);
				channel.read(dst);
				dst.flip();
				byte[] array = dst.array();
				for(byte b:array){
					System.out.print(" " + b);
				}
				System.out.println("");
				dst.clear();
				dst.put(array);
				dst.flip();
				channel.write(dst);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 7778));
		ByteBuffer dst = ByteBuffer.allocate(20);
		dst.put(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		dst.flip();
		channel.write(dst);
		dst.clear();
		channel.read(dst);
		for(byte b:dst.array()){
			System.out.print(" " + b);
		}
	}
}
