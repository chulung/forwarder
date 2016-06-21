package com.chulung.forwarder.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.KeysBuild;

public class RemoteToServerWriter extends AbstractForwarder implements Runnable {
	private byte[] keyBytes = KeysBuild.getBytesKeys();
	private SocketChannel remoteWithTargetChannel;

	public RemoteToServerWriter(SocketChannel forwardServerChannel, SocketChannel remoteWithTargetChannel) {
		super(forwardServerChannel);
		this.remoteWithTargetChannel = remoteWithTargetChannel;
	}

	@Override
	public void run() {
		int size = 0;
		dst = ByteBuffer.allocate(5000);
		boolean reading = true;
		while (reading) {
			dst.put(keyBytes);
			try {
				size = remoteWithTargetChannel.read(dst);
			} catch (IOException e1) {
				e1.printStackTrace();
				reading = false;
			}
			if (size >= 0) {
				dst.flip();
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
