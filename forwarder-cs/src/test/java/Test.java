import com.chulung.forwarder.proxy.ServerProxy;
import com.chulung.forwarder.server.ForwarderServer;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		new ForwarderServer().startAsync();
		Thread.sleep(1000);
		new ServerProxy().startSync();
	}
}
