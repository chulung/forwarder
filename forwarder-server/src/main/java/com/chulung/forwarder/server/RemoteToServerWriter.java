package com.chulung.forwarder.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;

public class RemoteToServerWriter extends AbstractForwarder implements Runnable {
	private byte[] keyBytes ;
	private SocketChannel remoteWithTargetChannel;

	public RemoteToServerWriter(SocketChannel forwardServerChannel, SocketChannel remoteWithTargetChannel,byte [] keyBytes) {
		super(forwardServerChannel);
		this.remoteWithTargetChannel = remoteWithTargetChannel;
		this.keyBytes=keyBytes;
	}

	@Override
	public void run() {
		dst = ByteBuffer.allocate(5000);
		int size = 0;
		boolean reading = true;
		while (reading) {
			dst.put(keyBytes);
			if (size >= 0) {
				try {
					size = remoteWithTargetChannel.read(dst);
				} catch (IOException e1) {
					e1.printStackTrace();
					reading = false;
				}
				dst.flip();
				logger.debug("remote key={} 推送至目标服务器", new String(keyBytes));
				try {
					forwardServerChannel.write(dst);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			dst.clear();
		}
	}

}
