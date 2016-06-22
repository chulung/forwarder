package com.chulung.forwarder.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;

public class ClientToServerWriter extends AbstractForwarder implements Runnable {
	private SocketChannel remoteWithTargetChannel;
	private byte[] remoteClientKeyBytes;

	public ClientToServerWriter(SocketChannel forwardServerChannel,SocketChannel remoteWithTargetChannel, byte[] remoteClientKeyBytes) {
		super(forwardServerChannel);
		this.remoteWithTargetChannel = remoteWithTargetChannel;
		this.remoteClientKeyBytes=remoteClientKeyBytes;
	}

	@Override
	public void run() {
		dst = ByteBuffer.allocate(100000);
		int size = 0;
		logger.debug("remoteClientKey={} 第一次请求，启动新的回传目标服务器响应数据线程", new String(remoteClientKeyBytes));
		try {
			while (true) {
				dst.put(remoteClientKeyBytes);
				size = remoteWithTargetChannel.read(dst);
				if (size >= 0) {
					dst.flip();
					System.out.println(dst.limit());
					logger.debug("remoteClientKey={} 收到新的回传目标服务器响应数据 limit={} 开始发送至转发服务器", new String(remoteClientKeyBytes),dst.limit());
					forwardServerChannel.write(dst);
				}
				dst.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				remoteWithTargetChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
