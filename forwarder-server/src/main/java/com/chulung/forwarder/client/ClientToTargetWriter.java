package com.chulung.forwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.chulung.forwarder.AbstractForwarder;

public class ClientToTargetWriter extends AbstractForwarder implements Runnable {
	private boolean reading = true;

	public ClientToTargetWriter(SocketChannel forwardServerChannel,
			Map<String, SocketChannel> remoteWithTargetChannelMap) {
		this.forwardServerChannel = forwardServerChannel;
		this.remoteWithTargetChannelMap = remoteWithTargetChannelMap;
	}

	public void run() {
		dst = ByteBuffer.allocate(5000);
		int size = 0;
		while (reading) {
			try {
				size = forwardServerChannel.read(dst);
			} catch (IOException e1) {
				e1.printStackTrace();
				reading = false;
			}
			if (size >= 0) {
				dst.flip();
				byte[] remoteClientKeyBytes = new byte[3];
				dst.get(remoteClientKeyBytes);
				String remoteClientKey = new String(remoteClientKeyBytes);
				SocketChannel remoteWithTargetChannel = remoteWithTargetChannelMap.get(remoteClientKey);
				if (remoteWithTargetChannel == null) {
					// 新建remote 与target channel
					try {
						remoteWithTargetChannel = SocketChannel
								.open(new InetSocketAddress(targetServerHostName, targetServerPort));
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					remoteWithTargetChannelMap.put(remoteClientKey, remoteWithTargetChannel);
					new Thread(new ClientToServerWriter(remoteWithTargetChannel)).start();
				}
				try {
					remoteWithTargetChannel.write(dst);
				} catch (IOException e) {
					e.printStackTrace();
					try {
						remoteWithTargetChannel.close();
						remoteWithTargetChannelMap.remove(remoteClientKey);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			dst.clear();
		}
	}

}
