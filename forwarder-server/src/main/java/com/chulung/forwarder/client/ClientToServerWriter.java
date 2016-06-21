package com.chulung.forwarder.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;

public class ClientToServerWriter extends AbstractForwarder implements Runnable {
	private SocketChannel remoteWithTargetChannel;
	private byte[] remoteClientKeyBytes;

	public ClientToServerWriter(SocketChannel remoteWithTargetChannel) {
		super(remoteWithTargetChannel);
		this.remoteWithTargetChannel = remoteWithTargetChannel;
	}

	@Override
	public void run() {
		dst = ByteBuffer.allocate(100000);
		int size = 0;
		try {
			while (true) {
				dst.put(remoteClientKeyBytes);
				size = remoteWithTargetChannel.read(dst);
				if (size >= 0) {
					dst.flip();
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
