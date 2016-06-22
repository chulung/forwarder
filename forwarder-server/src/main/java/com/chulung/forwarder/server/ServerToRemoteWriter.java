package com.chulung.forwarder.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.KeysBuild;

public class ServerToRemoteWriter extends AbstractForwarder implements Runnable {

	public ServerToRemoteWriter(SocketChannel forwardServerChannel,
			Map<String, SocketChannel> remoteWithTargetChannelMap) {
		super(forwardServerChannel, remoteWithTargetChannelMap);
	}

	@Override
	public void run() {
		this.logger.debug("启动从目标服务客户端读取，写到remote 客户端线程");
		int size = 0;
		dst = ByteBuffer.allocate(100000);
		boolean reading = true;
		while (reading) {
			// dst.put(keyBytes);
			try {
				size = forwardServerChannel.read(dst);
			} catch (IOException e1) {
				e1.printStackTrace();
				reading = false;
			}
			if (size >= 0) {
				dst.flip();
				System.out.println(dst.limit());
				byte[] remoteClientKeyBytes = new byte[3];
				dst.get(remoteClientKeyBytes);
				String remoteClientKey = KeysBuild.toString(remoteClientKeyBytes);
				this.logger.debug("收到目标服务客户端数据 remoteKey={} dst limit={}", remoteClientKey,dst.limit());
				SocketChannel remoteWithTargetChannel = remoteWithTargetChannelMap.get(remoteClientKey);
				// TODO 无对应remote 时需关闭
				if (remoteWithTargetChannel != null) {
					try {
						remoteWithTargetChannel.write(dst);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					this.logger.error("未找到remote Channel remoteKey={} ", remoteClientKey);
				}
			}
			dst.clear();
		}
	}

}
