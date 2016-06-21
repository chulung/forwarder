package com.chulung.forwarder;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractForwarder {
	
	protected ByteBuffer dst;
	/**
	 * 链接至forwarder服务器的channel
	 */
	protected SocketChannel forwardServerChannel;

	/**
	 * 远程客户端与转发目标服务器channel key 远程客户端标识符，value 对应目标服务器channelchannel
	 */
	protected Map<String, SocketChannel> remoteWithTargetChannelMap = new ConcurrentHashMap<String, SocketChannel>();
	/**
	 * 转发目标服务器host 如果服务器在本机，即为127.0.0.1
	 */
	protected String targetServerHostName;
	/**
	 * 转发目标服务器端口
	 */
	protected int targetServerPort;

	/**
	 * forwarder服务器端口
	 */
	protected int forwarderPort = 7777;

	public AbstractForwarder(SocketChannel forwardServerChannel,
			Map<String, SocketChannel> remoteWithTargetChannelMap) {
		super();
		this.forwardServerChannel = forwardServerChannel;
		this.remoteWithTargetChannelMap = remoteWithTargetChannelMap;
	}

	public AbstractForwarder() {
		super();
	}

	public AbstractForwarder(SocketChannel forwardServerChannel) {
		this.forwardServerChannel = forwardServerChannel;
	}

}
