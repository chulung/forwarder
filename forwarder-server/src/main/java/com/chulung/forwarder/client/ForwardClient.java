package com.chulung.forwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.Constant;

public class ForwardClient extends AbstractForwarder {
	/**
	 * forwarder服务器host ip或域名
	 */
	private String forwarderHostName = "127.0.0.1";

	public ForwardClient() {
	}

	public void init() throws IOException {
		forwardServerChannel = SocketChannel.open(new InetSocketAddress(forwarderHostName, forwarderPort));
		ByteBuffer src = ByteBuffer.allocate(Constant.SERVER_HEAD.length());
		src.put(Constant.SERVER_HEAD.getBytes());
		// 发送消息头，标识本机为转发服务器客户端
		forwardServerChannel.write(src);
		new ConcurrentHashMap<String, SocketChannel>();
		new Thread(new ClientToTargetWriter(forwardServerChannel, remoteWithTargetChannelMap)).start();
	}

}
