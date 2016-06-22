package com.chulung.forwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.KeysBuild;

public class ClientToTargetWriter extends AbstractForwarder implements Runnable {
	private boolean reading = true;

	public ClientToTargetWriter(SocketChannel forwardServerChannel,
			Map<String, SocketChannel> remoteWithTargetChannelMap, String targetServerHostName, int targetServerPort) {
		this.forwardServerChannel = forwardServerChannel;
		this.remoteWithTargetChannelMap = remoteWithTargetChannelMap;
		this.targetServerHostName = targetServerHostName;
		this.targetServerPort = targetServerPort;
	}

	public void run() {
		logger.debug("从转发服务器发往目标服务器线程启动 ");
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
				String remoteClientKey = KeysBuild.toString(remoteClientKeyBytes);
				SocketChannel remoteWithTargetChannel = remoteWithTargetChannelMap.get(remoteClientKey);
				logger.debug("收到来自转发服务器的 请求 remoteClientKey={}", remoteClientKey);
				if (remoteWithTargetChannel == null) {
					// 新建remote 与target channel
					try {
						remoteWithTargetChannel = SocketChannel
								.open(new InetSocketAddress(targetServerHostName, targetServerPort));
						logger.debug("remoteClientKey={} 第一次请求，启动新socket链接目标服务器", remoteClientKey);
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					remoteWithTargetChannelMap.put(remoteClientKey, remoteWithTargetChannel);
					new Thread(new ClientToServerWriter(forwardServerChannel,remoteWithTargetChannel,remoteClientKeyBytes)).start();
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
