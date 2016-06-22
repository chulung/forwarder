package com.chulung.forwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.chulung.forwarder.AbstractForwarder;
import com.chulung.forwarder.Constant;

public class ForwardClient extends AbstractForwarder {
	public ForwardClient(String forwarderHostName,int forwarderPort, String targetServerHostName, int targetServerPort) {
		this.forwarderHostName = forwarderHostName;
		this.forwarderPort=forwarderPort;
		this.targetServerHostName=targetServerHostName;
		this.targetServerPort=targetServerPort;
	}

	public void init() throws IOException {
		forwardServerChannel = SocketChannel.open(new InetSocketAddress(forwarderHostName, forwarderPort));
		logger.debug("启动目标服务器客户端，转发服务器host {} ip {} 目标服务器host{} ip {}", forwarderHostName, forwarderPort,
				targetServerHostName, targetServerPort);
		ByteBuffer src = ByteBuffer.allocate(Constant.SERVER_HEAD.length());
		src.put(Constant.SERVER_HEAD.getBytes());
		src.flip(); 
		// 发送消息头，标识本机为目标服务器客户端
		forwardServerChannel.write(src);
		logger.debug("发送消息头，标识本机为转发服务器客户端 SERVER_HEAD={}", Constant.SERVER_HEAD);
		new ClientToTargetWriter(forwardServerChannel, remoteWithTargetChannelMap,targetServerHostName,targetServerPort).run();
	}

	public String getForwarderHostName() {
		return forwarderHostName;
	}

	public void setForwarderHostName(String forwarderHostName) {
		this.forwarderHostName = forwarderHostName;
	}

	public void start() throws IOException {
		this.init();
	}

}
