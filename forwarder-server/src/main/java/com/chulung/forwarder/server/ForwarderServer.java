package com.chulung.forwarder.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.Constant;

public class ForwarderServer extends AbstractForwarder {
	public ForwarderServer() {

	}

	public void start() throws IOException {
		ServerSocketChannel listenerChannel = ServerSocketChannel.open();
		// 与本地端口绑定
		listenerChannel.socket().bind(new InetSocketAddress(forwarderPort));
		while (true) {
			SocketChannel clientChannel = listenerChannel.accept();
			if (clientChannel != null) {
				if (this.forwardServerChannel == null) {
					ByteBuffer dst = ByteBuffer.allocate(Constant.SERVER_HEAD.length());
					int size = clientChannel.read(dst);
					if (size > 0 && Constant.SERVER_HEAD.equals(new String(dst.array()))) {
						this.forwardServerChannel = clientChannel;
						new Thread(new ServerToRemoteWriter(forwardServerChannel,remoteWithTargetChannelMap)).start();
					}
				}
			}
		}
	}
}
