package com.chulung.forwarder.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.chulung.forwarder.AbstractForwarder;

public class ServerToRemoteWriter extends AbstractForwarder implements Runnable {

	public ServerToRemoteWriter(SocketChannel forwardServerChannel,
			Map<String, SocketChannel> remoteWithTargetChannelMap) {
		super(forwardServerChannel, remoteWithTargetChannelMap);
	}

	@Override
	public void run() {
		int size = 0;
		dst = ByteBuffer.allocate(5000);
		boolean reading = true;
		while (reading) {
//			dst.put(keyBytes);
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
