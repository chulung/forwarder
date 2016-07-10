import com.chulung.forwarder.proxy.ClientProxy;
import com.chulung.forwarder.proxy.ServerProxy;
import com.chulung.forwarder.server.ForwarderServer;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		new Thread(() -> {
			new ForwarderServer().start();
		}).start();
		Thread.sleep(1000);
		new Thread(() -> {
			new ServerProxy().run();
		}).start();;
		Thread.sleep(1000);
		new ClientProxy().run();
	}
}
