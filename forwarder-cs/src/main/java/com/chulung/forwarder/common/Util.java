package com.chulung.forwarder.common;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;

public class Util {
	public static int getLocalPort(ChannelHandlerContext channelHandlerContext) {
		return ((InetSocketAddress) channelHandlerContext.channel().localAddress()).getPort();
	}
}
