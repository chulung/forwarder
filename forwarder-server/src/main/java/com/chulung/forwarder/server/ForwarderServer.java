package com.chulung.forwarder.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.Constant;
import com.chulung.forwarder.KeysBuild;

public class ForwarderServer extends AbstractForwarder {
	public ForwarderServer(int port) {
		this.setForwarderPort(port);
	}

	public void start() throws IOException {
		ServerSocketChannel listenerChannel = ServerSocketChannel.open();
		// 与本地端口绑定
		listenerChannel.socket().bind(new InetSocketAddress(forwarderPort));
		logger.debug("server 端口绑定监听 {}", forwarderPort);
		while (true) {
			SocketChannel clientChannel = listenerChannel.accept();
			if (clientChannel != null) {
				if (this.forwardServerChannel == null) {
					ByteBuffer dst = ByteBuffer.allocate(Constant.SERVER_HEAD.length());
					logger.debug("收到客户端请求{}", dst);
					int size = clientChannel.read(dst);
					if (forwardServerChannel == null && size > 0
							&& Constant.SERVER_HEAD.equals(new String(dst.array(), 0, Constant.SERVER_HEAD.length()))) {
						logger.debug("客户端为目标服务器客户端");
						this.forwardServerChannel = clientChannel;
						new Thread(new ServerToRemoteWriter(forwardServerChannel, remoteWithTargetChannelMap)).start();
					}
				} else {
					byte[] key = KeysBuild.getBytesKeys();
					this.remoteWithTargetChannelMap.put(KeysBuild.toString(key), clientChannel);
					new Thread(new RemoteToServerWriter(forwardServerChannel, clientChannel, key)).start();
				}
			}
		}
	}
}
